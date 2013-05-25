package com.mypopescu.jreql.internal;


import com.mypopescu.jreql.JsonObject;
import com.mypopescu.jreql.ReqlProto;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author alex
 * @version 1.0, 5/19/13 11:39 AM
 */
public class TestDatumConverter {
    @Test
    public void parseInt() {
        int i= DatumConverter.getInt(ReqlProto.Datum.newBuilder().setType(ReqlProto.Datum.DatumType.R_NUM).setRNum(11).build());

        assertEquals(11, i);
    }

    @Test
    public void parseLong() {
        double v= 2147483648d;
        long lg= DatumConverter.getLong(ReqlProto.Datum.newBuilder().setType(ReqlProto.Datum.DatumType.R_NUM).setRNum(v).build());

        assertEquals((long) v, lg);
    }

    @Test
    public void parseDouble() {
        double v= 1.234e2;
        Double d= DatumConverter.getDouble(ReqlProto.Datum.newBuilder().setType(ReqlProto.Datum.DatumType.R_NUM).setRNum(v).build());

        assertEquals(v, d, 0.0);
    }

    @Test
    public void parseBool() {
        boolean b= DatumConverter.getBool(ReqlProto.Datum.newBuilder().setType(ReqlProto.Datum.DatumType.R_BOOL).setRBool(true).build());

        assertEquals(true, b);
    }

    @Test
    public void parseStr() {

    }

    @Test
    public void parseIntArray() {
        int[] intArray = DatumConverter.getIntArray(ReqlProto.Datum.newBuilder().setType(ReqlProto.Datum.DatumType.R_ARRAY)
            .addRArray(ReqlProto.Datum.newBuilder().setType(ReqlProto.Datum.DatumType.R_NUM).setRNum(1).build())
            .addRArray(ReqlProto.Datum.newBuilder().setType(ReqlProto.Datum.DatumType.R_NUM).setRNum(2).build())
            .addRArray(ReqlProto.Datum.newBuilder().setType(ReqlProto.Datum.DatumType.R_NUM).setRNum(3).build())
            .build()
        );

        assertArrayEquals(new int[] {1, 2, 3}, intArray);
    }
}
