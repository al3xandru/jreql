package com.mypopescu.jreql.internal;


import com.mypopescu.jreql.JsonObject;
import com.mypopescu.jreql.ReqlException;
import com.mypopescu.jreql.ReqlProto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An utility class to convert between native types and <code>ReqlProto.Datum</code>.
 *
 * @author alex
 * @version 1.0, 5/19/13 1:23 PM
 */
public class DatumConverter {
    public static int getInt(ReqlProto.Datum datum) {
        return (int) datum.getRNum();
    }

    public static long getLong(ReqlProto.Datum datum) {
        return (long) datum.getRNum();
    }

    public static double getDouble(ReqlProto.Datum datum) {
        return datum.getRNum();
    }

    public static boolean getBool(ReqlProto.Datum datum) {
        return datum.getRBool();
    }

    public static String getString(ReqlProto.Datum datum) {
        return datum.getRStr();
    }

    public static boolean isNull(ReqlProto.Datum datum) {
        return datum.getType() == ReqlProto.Datum.DatumType.R_NULL;
    }

    // array
    public static int[] getIntArray(ReqlProto.Datum datum) {
        List<ReqlProto.Datum> values= datum.getRArrayList();
        int[] result= new int[values.size()];
        int idx= 0;
        for(ReqlProto.Datum d: values) {
            result[idx++]= (int) d.getRNum();
        }

        return result;
    }

    public static long[] getLongArray(ReqlProto.Datum datum) {
        List<ReqlProto.Datum> values= datum.getRArrayList();
        long[] result= new long[values.size()];
        int idx= 0;
        for(ReqlProto.Datum d: values) {
            result[idx++]= (long) d.getRNum();
        }

        return result;
    }

    public static double[] getDoubleArray(ReqlProto.Datum datum) {
        List<ReqlProto.Datum> values= datum.getRArrayList();
        double[] result= new double[values.size()];
        int idx= 0;
        for(ReqlProto.Datum d: values) {
            result[idx++]= d.getRNum();
        }

        return result;
    }

    public static boolean[] getBoolArray(ReqlProto.Datum datum) {
        List<ReqlProto.Datum> values= datum.getRArrayList();
        boolean[] result= new boolean[values.size()];
        int idx= 0;
        for(ReqlProto.Datum d: values) {
            result[idx++]= d.getRBool();
        }

        return result;
    }

    public static String[] getStringArray(ReqlProto.Datum datum) {
        String[] result= new String[datum.getRArrayCount()];
        int idx= 0;
        for(ReqlProto.Datum d: datum.getRArrayList()) {
            result[idx++]= d.getRStr();
        }

        return result;
    }

    public static JsonObject[] getObjArray(ReqlProto.Datum datum) {
        JsonObject[] result= new JsonObject[datum.getRArrayCount()];
        int idx= 0;
        for(ReqlProto.Datum d: datum.getRArrayList()) {
            result[idx++]= getObj(d);
        }

        return result;
    }

    public static Object[] getArray(ReqlProto.Datum datum) {
        Object[] result= new Object[datum.getRArrayCount()];
        int idx= 0;
        for(ReqlProto.Datum d: datum.getRArrayList()) {
            result[idx++]= get(d);
        }

        return result;
    }

    // objects
    public static JsonObject getObj(ReqlProto.Datum datum) {
        return new JsonObject(datum.getRObjectList());
    }

    /**
     * Most generic converter method.
     *
     * @param data the <code>Datum</code> to convert to native types
     * @param <T> the generic autocast return type
     *
     * @return a native type value
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(ReqlProto.Datum data) {

        switch(data.getType()) {
            case R_NULL:
                return null;
            case R_BOOL:
                return (T) Boolean.valueOf(data.getRBool());
            case R_STR:
                return (T) data.getRStr();
            case R_NUM:
                double dv= data.getRNum();
                Double dobj= dv;
                if(dv == dobj.intValue()) {
                    return (T) Integer.valueOf(dobj.intValue());
                }
                else if(dv == dobj.longValue()) {
                    return (T) Long.valueOf(dobj.longValue());
                }
                return (T) dobj;
            case R_ARRAY:
                Object[] arr= new Object[data.getRArrayCount()];
                int idx= 0;
                for(ReqlProto.Datum d: data.getRArrayList()) {
                    arr[idx++] = get(d);
                }
                return (T) arr;
            case R_OBJECT:
                return (T) new JsonObject(data.getRObjectList());
        }

        throw new ReqlException("Unknown data type", data);
    }

    @SuppressWarnings("unchecked")
    public static <T> T deepConvert(ReqlProto.Datum data) {
        switch(data.getType()) {
            case R_ARRAY:
                Object[] arr= new Object[data.getRArrayCount()];
                int idx= 0;
                for(ReqlProto.Datum d: data.getRArrayList()) {
                    if(ReqlProto.Datum.DatumType.R_OBJECT == d.getType()) {
                        arr[idx++]= deepConvert(d);
                    }
                    else if(ReqlProto.Datum.DatumType.R_ARRAY == d.getType()) {
                        arr[idx++]= deepConvert(d);
                    }
                    else {
                        arr[idx++]= get(d);
                    }
                }
                return (T) arr;
            case R_OBJECT:
                Map<String, Object> res= new HashMap<String, Object>(data.getRObjectCount());
                for(ReqlProto.Datum.AssocPair d: data.getRObjectList()) {
                    ReqlProto.Datum val= d.getVal();
                    if(ReqlProto.Datum.DatumType.R_OBJECT == val.getType()) {
                        res.put(d.getKey(), deepConvert(val));
                    }
                    else if(ReqlProto.Datum.DatumType.R_ARRAY == d.getVal().getType()) {
                        res.put(d.getKey(), deepConvert(val));
                    }
                    else {
                        res.put(d.getKey(), get(val));
                    }
                }
                return (T) res;
            default:
                return get(data);
        }
    }

}
