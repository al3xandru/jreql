package com.mypopescu.jreql;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author alex
 * @version 1.0, 5/19/13 2:59 AM
 */
public class TestSingleResultResponse {
    private ResultSet getResultSet(ReqlProto.Datum.Builder db) {
        ReqlProto.Response.Builder builder= ReqlProto.Response.newBuilder()
            .setType(ReqlProto.Response.ResponseType.SUCCESS_ATOM)
            .addResponse(db.build())
            ;

        Connection connection = new Connection("example.org", 28015);
        ResultSet rs = connection.parseResponse(null,
            ReqlProto.Query.QueryType.START,
            null,
            -1,
            builder.build());

        return rs;
    }

    @Test
    public void assumptions() {
        ResultSet rs= getResultSet(ReqlProto.Datum.newBuilder().setRBool(true));

        assertFalse(rs.isEmpty());
        assertTrue(rs.next());
        assertFalse(rs.next());
        assertTrue(rs.first());
    }

    @Test
    public void getBoolean() {
        ResultSet rs= getResultSet(ReqlProto.Datum.newBuilder().setRBool(false));
        rs.first();

        assertFalse(rs.getBoolean());
    }

    @Test
    public void getInt() {
        ResultSet rs= getResultSet(ReqlProto.Datum.newBuilder().setRNum(101));
        rs.first();

        assertEquals(101, rs.getInt());
    }

    @Test
    public void getLong() {
        double d = 21474836478L;
        ResultSet rs= getResultSet(ReqlProto.Datum.newBuilder().setRNum(d));
        rs.first();

        assertEquals((long) d, rs.getLong());
    }


    @Test
    public void getDouble() {
        ResultSet rs= getResultSet(ReqlProto.Datum.newBuilder().setRNum(2.32));
        rs.first();

        assertEquals(2.32, rs.getDouble(), 0);
    }


    @Test
    public void getString() {
        ResultSet rs= getResultSet(ReqlProto.Datum.newBuilder().setRStr("str"));
        rs.first();

        assertEquals("str", rs.getString());
    }

    @Test
    public void getIntArray() {
        ResultSet rs= getResultSet(ReqlProto.Datum.newBuilder()
            .addRArray(ReqlProto.Datum.newBuilder().setRNum(1).build())
            .addRArray(ReqlProto.Datum.newBuilder().setRNum(2).build())
            .addRArray(ReqlProto.Datum.newBuilder().setRNum(72).build())
        );
        rs.first();

        assertArrayEquals(new int[] {1, 2, 72}, rs.getIntArray());
    }

    @Ignore("not implemented yet")
    @Test
    public void getDoubleArray() {
    }

    @Ignore("not implemented yet")
    @Test
    public void getBooleanArray() {
    }

    @Ignore("not implemented yet")
    @Test
    public void getStringArray() {
    }

    @Ignore("not implemented yet")
    @Test
    public void getLongArray() {
    }

    @Ignore("not implemented yet")
    @Test
    public void getObjectBasic() {
    }

    @Ignore("not implemented yet")
    @Test
    public void getObjectComplex() {
    }

    @Ignore("not implemented yet")
    @Test
    public void getList() {
    }

    @Ignore("not implemented yet")
    @Test
    public void getObjectList() {
    }
}
