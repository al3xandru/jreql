package com.mypopescu.jreql;

import java.util.Arrays;
import java.util.List;

/**
 * @author alex
 * @version 1.0, 5/19/13 12:48 AM
 */
public class ResultSet {
    private final Connection m_connection;
    private final ReqlProto.Term m_query;
    private final int m_token;
    private final boolean m_complete;

    private List<ReqlProto.Datum> m_data;
    protected int m_currentIndex;

    public ResultSet(Connection c, ReqlProto.Term query, int token, List<ReqlProto.Datum> data, boolean complete) {
        m_connection= c;
        m_query= query;
        m_token= token;
        m_complete = complete;
        m_currentIndex= -1;
        m_data= data;
    }

    public boolean next() {
        if (m_data == null) {
            return false;
        }
        m_currentIndex++;

        if(!m_complete && m_currentIndex == m_data.size()) {
            m_connection.run(m_query, ReqlProto.Query.QueryType.CONTINUE, this);
        }
        return m_currentIndex < m_data.size() - 1;
    }

    public boolean first() {
        if (m_data == null) {
            return false;
        }

        m_currentIndex = 0;
        return true;
    }

    public void close() {
        m_connection.run(m_query, ReqlProto.Query.QueryType.STOP, this);
    }


    public boolean isEmpty() {
        return m_data == null || m_data.size() == 0;
    }

    public boolean getBoolean() {
        return m_data.get(m_currentIndex).getRBool();
    }

    public int getInt() {
        return (int) m_data.get(m_currentIndex).getRNum();
    }

    public long getLong() {
        return (int) m_data.get(m_currentIndex).getRNum();
    }

    public double getDouble() {
        return m_data.get(m_currentIndex).getRNum();
    }

    public String getString() {
        return m_data.get(m_currentIndex).getRStr();
    }

    public int[] getIntArray() {
        List<ReqlProto.Datum> elements = m_data.get(m_currentIndex).getRArrayList();
        int[] result= new int[elements.size()];
        for(int i=0; i < elements.size(); i++) {
            result[i] = (int) elements.get(i).getRNum();
        }

        return result;
    }

    public double[] getDoubleArray() {
        List<ReqlProto.Datum> elements = m_data.get(m_currentIndex).getRArrayList();
        double[] result= new double[elements.size()];
        for(int i=0; i < elements.size(); i++) {
            result[i] = elements.get(i).getRNum();
        }

        return result;
    }

    public boolean[] getBooleanArray() {
        List<ReqlProto.Datum> elements = m_data.get(m_currentIndex).getRArrayList();
        boolean[] result= new boolean[elements.size()];
        for(int i=0; i < elements.size(); i++) {
            result[i] = elements.get(i).getRBool();
        }

        return result;
    }

    public String[] getStringArray() {
        List<ReqlProto.Datum> elements = m_data.get(m_currentIndex).getRArrayList();
        String[] result= new String[elements.size()];
        for(int i=0; i < elements.size(); i++) {
            result[i] = elements.get(i).getRStr();
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList() {
        return null;
    }

    protected int getToken() {
        return m_token;
    }

    protected ResultSet append(List<ReqlProto.Datum> data) {
        m_data.addAll(data);
        return this;
    }

    public static class SingleResultResultSet extends ResultSet {
        public SingleResultResultSet(Connection c, ReqlProto.Term query, int token, ReqlProto.Datum data) {
            super(c, query, token, Arrays.asList(data), true);
        }

        public boolean next() {
            m_currentIndex++;
            return m_currentIndex == 0;
        }

        public void close() {
            // no-op
        }
    }

    public static class EmptyResultSet extends SingleResultResultSet {
        public EmptyResultSet(Connection c, ReqlProto.Term query, int token) {
            super(c, query, token, null);
        }

        public boolean isEmpty() {
            return true;
        }

        public boolean next() {
            return false;
        }

        public boolean first() {
            return false;
        }

        public void close() {
            // no-op
        }
    }
}
