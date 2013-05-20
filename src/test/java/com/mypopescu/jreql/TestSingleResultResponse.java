package com.mypopescu.jreql;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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

        return connection.parseResponse(null,
            ReqlProto.Query.QueryType.START,
            null,
            -1,
            builder.build());
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
    public void getBool() {
        ResultSet rs= getResultSet(ReqlProto.Datum.newBuilder().setRBool(false));
        rs.first();

        assertFalse(rs.getBool());
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

    @Test
    public void getObjectBasic() {
        ResultSet rs= getResultSet(ReqlProto.Datum.newBuilder()
            .addRObject(ReqlProto.Datum.AssocPair.newBuilder().setKey("anInt").setVal(ReqlProto.Datum.newBuilder().setRNum(8).build()).build())
            .addRObject(ReqlProto.Datum.AssocPair.newBuilder().setKey("aDouble").setVal(ReqlProto.Datum.newBuilder().setRNum(8.88).build()).build())
            .addRObject(ReqlProto.Datum.AssocPair.newBuilder().setKey("aBool").setVal(ReqlProto.Datum.newBuilder().setRBool(true).build()).build())
            .addRObject(ReqlProto.Datum.AssocPair.newBuilder().setKey("aStr").setVal(ReqlProto.Datum.newBuilder().setRStr("stringvalue").build()).build())
            .addRObject(ReqlProto.Datum.AssocPair.newBuilder().setKey("aNull").build())
        );
        rs.first();

        JsonObject obj= rs.getObj();

        assertNotNull(obj);
        assertEquals(8, obj.getInt("anInt"));
        assertEquals(8.88, obj.getDouble("aDouble"), 0);
        assertEquals(true, obj.getBool("aBool"));
        assertEquals("stringvalue", obj.getString("aStr"));
        assertTrue(obj.isNull("aNull"));
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
