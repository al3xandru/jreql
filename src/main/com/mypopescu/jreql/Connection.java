package com.mypopescu.jreql;


import com.google.protobuf.AbstractMessage;
import com.google.protobuf.InvalidProtocolBufferException;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

/**
 *
 * Credit: part of this code has been inspired by
 * <a href="">Riak's Java driver</a>.
 */
public class Connection {
    private Config m_config;
    private SocketAddress m_address;
    private Socket m_socket;
    private DataOutputStream m_dos;
    private DataInputStream m_dis;
    private int m_token;
    private List<ResultSet> m_openResultSets = new ArrayList<ResultSet>();

    public Connection(String host, int port) {
        this(host, port, DEFAULT_CONFIG);
    }

    public Connection(String host, int port, Config cfg) {
        m_config = cfg;
        m_address = new InetSocketAddress(host, port);
        m_token = 1;
    }

    /**
     * Connect to the server.
     *
     * @throws ConnectionException if either there's a misconfiguration
     * or the connection couldn't be established.
     */
    public Connection connect() throws ConnectionException {
        m_socket = new Socket();

        // enabled/disable SO_TIMEOUT used for read
        try {
            if (m_config.getRequestTimeout() > 0) {
                m_socket.setSoTimeout(m_config.getRequestTimeout());
            }
            // TODO: check which of these makes sense
            m_socket.setKeepAlive(true);
            m_socket.setSendBufferSize(m_config.getSendBufferSize());
            m_socket.setReceiveBufferSize(m_config.getRecvBufferSize());
        }
        catch(SocketException socex) {
            throw new ConnectionException("Failed to configure connection socket", socex, m_config);
        }

        try {
            m_socket.connect(m_address, m_config.getConnectionTimeout());

            m_dos = new DataOutputStream(new BufferedOutputStream(m_socket.getOutputStream(), m_config.getSendBufferSize()));
            m_dis = new DataInputStream(new BufferedInputStream(m_socket.getInputStream(), m_config.getRecvBufferSize()));

            sendVersion();
        }
        catch(IOException ioex) {
            throw new ConnectionException("Cannot create connection", ioex);
        }

        return this;
    }

    /**
     * Reconnect to the server: firstly calls {@link #close()}
     * and then {@link #connect()}.
     *
     * @throws ConnectionException
     */
    public void reconnect() throws ConnectionException {
        close();
        connect();
    }

    /**
     * Close connection to the server (closes the
     * underlying <code>socket</code>).
     *
     * @throws ConnectionException if closing the underlying <code>socket</code>
     * resuled in an exception
     */
    public void close() throws ConnectionException {
        if(isClosed()) return;

        for(ResultSet rs : m_openResultSets) {
            try {
                rs.close();
            }
            catch(ReqlException ex) {
                ;
            }
        }
        try {
            m_socket.close();
        }
        catch (IOException ioex) {
            throw new ConnectionException("An exception occurred closing the underlying socket", ioex);
        }
        finally {
            m_dis = null;
            m_dos = null;
            m_socket = null;
        }
    }

    /**
     * Checks if the underlying <code>socket</code>
     * was closed.
     *
     * @return <code>true</code> if the underlying <code>socket</code>
     * was closed or <code>null</code>.
     */
    public boolean isClosed() {
        return m_socket == null || m_socket.isClosed();
    }


    /**
     * Execute the query.
     *
     * @param query the protobuf term representing the query
     * @param queryType can be one of {@link ReqlProto.Query.QueryType#START}
     *                  or {@link ReqlProto.Query.QueryType#CONTINUE}
     *                  or {@link ReqlProto.Query.QueryType#STOP}
     * @param existingRS used only by {@link ReqlProto.Query.QueryType#CONTINUE} queries
     *
     * @return the {@link ResultSet}
     */
    protected ResultSet run(ReqlProto.Term query, ReqlProto.Query.QueryType queryType, ResultSet existingRS) {
        int token = queryType == ReqlProto.Query.QueryType.START ?
            ++m_token : existingRS.getToken();

        ReqlProto.Query.Builder qb = buildQuery(query, queryType, token);

        send(qb.build());
        ReqlProto.Response resp = recv();

        if (m_token != resp.getToken()) {
            throw new ReqlException("Unexpected response received (token differ: query", null);
        }

        ResultSet rs= parseResponse(query, queryType, existingRS, token, resp);

        m_openResultSets.add(rs);

        return rs;
    }

    protected ReqlProto.Query.Builder buildQuery(ReqlProto.Term query,
                                                 ReqlProto.Query.QueryType queryType,
                                                 int token) {
        ReqlProto.Query.Builder qb = ReqlProto.Query.newBuilder()
            .setToken(token)
            .setType(queryType)
            .setQuery(query);

        qb.addGlobalOptargs(
            ReqlProto.Query.AssocPair.newBuilder()
                .setKey("db")
                .setVal(ReqlProto.Term.newBuilder()
                    .setType(ReqlProto.Term.TermType.DB)
                    .addArgs(ReqlProto.Term.newBuilder()
                        .setType(ReqlProto.Term.TermType.DATUM)
                        .setDatum(ReqlProto.Datum.newBuilder()
                            .setType(ReqlProto.Datum.DatumType.R_STR)
                            .setRStr("test")
                            .build())
                        .build())
                    .build())
                .build());

        return qb;
    }

    protected ResultSet parseResponse(ReqlProto.Term query,
                                      ReqlProto.Query.QueryType queryType,
                                      ResultSet existingRS,
                                      int token,
                                      ReqlProto.Response resp) {
        switch(resp.getType()) {
            case CLIENT_ERROR:
                throw new QueryClientException(resp.getResponse(0).getRStr(), resp.getBacktrace());
            case COMPILE_ERROR:
                throw new QueryCompileException(resp.getResponse(0).getRStr(), resp.getBacktrace());
            case RUNTIME_ERROR:
                throw new QueryRuntimeException(resp.getResponse(0).getRStr(), resp.getBacktrace());
        }

        ResultSet resultSet;

        switch(resp.getType()) {
            case SUCCESS_PARTIAL:
                if (queryType == ReqlProto.Query.QueryType.START) {
                    resultSet = new ResultSet(this, query, token, resp.getResponseList(), false);
                }
                else {
                    resultSet= existingRS;
                    resultSet.append(resp.getResponseList());
                }
                break;
            case SUCCESS_SEQUENCE:
                resultSet = new ResultSet(this, query, token, resp.getResponseList(), true);
                break;
            case SUCCESS_ATOM:
                if(resp.getResponseCount() == 0) {
                    resultSet = new ResultSet.EmptyResultSet(this, query, token);
                }
                else {
                    resultSet = new ResultSet.SingleResultResultSet(this, query, token, resp.getResponse(0));
                }
                break;
            default:
                throw new ReqlException(format("Unknown response type: %s", resp.getType()), resp);
        }

        return resultSet;
    }

    protected void send(AbstractMessage msg) {
        try {
            m_dos.write(toLittleEndian(msg.getSerializedSize()));
            msg.writeTo(m_dos);
            m_dos.flush();
        }
        catch(IOException ioex) {
            throw new ConnectionException("Failed to send message", ioex, msg);
        }
    }

    protected ReqlProto.Response recv() {
        int msgSize = readLength();
//        System.out.printf("Response size: %d%n", msgSize);
        byte[] data = new byte[msgSize];

        try {
            m_dis.readFully(data);
            return ReqlProto.Response.parseFrom(data);
        }
        catch(EOFException eofex) {
            throw new ConnectionException("The connection was closed", eofex);
        }
        catch(InvalidProtocolBufferException ipbex) {
            throw new ReqlException("Cannot parse response", ipbex, data);
        }
        catch(IOException ioex) {
            throw new ConnectionException(ioex, null);
        }
    }


    protected int readLength() throws ConnectionException {
        byte[] responseSize = new byte[4];
        int len;
        try {
            len = m_dis.read(responseSize, 0, 4);
        }
        catch(IOException ioex) {
            throw new ConnectionException(ioex, null);
        }
        if (len < 4) {
            throw new ConnectionException(format("Expecting length of message (4bytes), received only %s", len), null);
        }
        return toBigEndian(responseSize);
    }

    protected void sendVersion() throws ConnectionException {
        try {
            m_dos.write(toLittleEndian(ReqlProto.VersionDummy.Version.V0_1_VALUE));
            m_dos.flush();
        }
        catch (IOException ioex) {
            throw new ConnectionException(ioex, null);
        }

    }

    protected byte[] toLittleEndian(int value) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(value);
        return bb.array();
    }

    protected int toBigEndian(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(bytes);
        bb.rewind();
        return bb.getInt();
    }

    /**
     * Configuration options for a database connection.
     * The following options are supported:
     *
     * <ul>
     *     <li><code>requestTimeout</code> (millis): <code>SO_TIMEOUT</code> (read timeout). If non-zero reads will block
     *     for at most the given milliseconds. If zero, then <code>SO_TIMEOUT</code> is disabled
     *     (reads block indefinitely)
     *     </li>
     *     <li><code>connectionTimeout</code> (millis): the timeout to obtain a connection to the server.</li>
     *     <li><code>sendBufferSize</code> and <code>recvBufferSize</code> (bytes): buffer sizes for sending
     *     and receiving respectively.</li>
     * </ul>
     */
    public static class Config {
        private final int requestTimeout;
        private final int connectionTimeout;
        private final int sendBufferSize;
        private final int recvBufferSize;

        public Config(int readTimeoutMillis, int connectTimeoutMillis, int sendBufSize, int recvBufSize) {
            requestTimeout = readTimeoutMillis;
            connectionTimeout = connectTimeoutMillis;
            sendBufferSize = sendBufSize;
            recvBufferSize = recvBufSize;
        }

        public int getRequestTimeout() {
            return requestTimeout;
        }

        public int getConnectionTimeout() {
            return connectionTimeout;
        }

        public int getSendBufferSize() {
            return sendBufferSize;
        }

        public int getRecvBufferSize() {
            return recvBufferSize;
        }
    }

    /**
     * Default connection configuration:
     *
     * <ul>
     * <li><code>requestTimeout</code>: 0 (<code>SO_TIMEOUT</code> disabled)</li>
     * <li><code>connectionTimeout</code>: 1000 millis</li>
     * <li><code>sendBufferSize</code> and <code>recvBufferSize</code>: 1024 bytes</li>
     * </ul>
     */
    private static final Config DEFAULT_CONFIG = new Config(0, 1000, 1024, 1024);
}
