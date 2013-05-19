package com.mypopescu.jreql;


import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


/**
 * User: alex
 * Date: 3/21/13 8:52 AM
 */
@RunWith(Parameterized.class)
public class TestConnection {
    private String m_host;
    private int m_port;

    public TestConnection(String host, int port) {
        m_host = host;
        m_port = port;
    }

    @Ignore("it fails to catch its own exception")
    @Test(timeout=1000, expected=Exception.class)
    public void failedConnectionNoTimeout() throws IOException {
        new Connection("example.org", 28015).connect();
        fail("test should timeout");
    }

    @Test(timeout=2000, expected=ConnectionException.class)
    public void failedConnectionWithTimeout() throws IOException {
        new Connection("example.org", 28015, new Connection.Config(0, 1000, 1024, 1024)).connect();
        fail("test should timeout");
    }

    @Test(timeout=5000)
    public void connect() throws IOException {
        Connection c = new Connection(m_host, m_port).connect();
        assertFalse(c.isClosed());
        c.close();
    }

    @Parameterized.Parameters(name="{index}: host:{0} port:{1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {"192.168.97.162", 28015}
        });
    }

    @Test
    public void query() throws IOException {
        ReqlProto.Query q = ReqlProto.Query.newBuilder()
                .setType(ReqlProto.Query.QueryType.START)
                .setToken(1)
                .build();
        Connection c = new Connection(m_host, m_port).connect();
        c.send(q);
        assertNotNull(c.recv());
        c.close();
    }
}
