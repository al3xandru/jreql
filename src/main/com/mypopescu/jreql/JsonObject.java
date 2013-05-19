package com.mypopescu.jreql;


import com.mypopescu.jreql.internal.DatumConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A lazy-JSON-like object built around <code>Datum</code>.
 *
 * @author alex
 * @version 1.0, 5/19/13 11:17 AM
 */
public class JsonObject  {
    private Map<String, ReqlProto.Datum> m_data= new HashMap<String, ReqlProto.Datum>();

    public JsonObject() {
    }

    public JsonObject(List<ReqlProto.Datum.AssocPair> pairs) {
        for(ReqlProto.Datum.AssocPair kv: pairs) {
            m_data.put(kv.getKey(), kv.getVal());
        }
    }

    public int getInt(String key) {
        return DatumConverter.getInt(m_data.get(key));
    }

    public int getInt(String key, int defaultValue) {
        ReqlProto.Datum val= m_data.get(key);

        return val == null ? defaultValue : DatumConverter.getInt(val);
    }

    public long getLong(String key) {
        return DatumConverter.getLong(m_data.get(key));
    }

    public long getLong(String key, long defaultValue) {
        ReqlProto.Datum val= m_data.get(key);

        return val == null ? defaultValue : DatumConverter.getLong(val);
    }

    public double getDouble(String key) {
        return DatumConverter.getDouble(m_data.get(key));
    }

    public double getDouble(String key, double defaultValue) {
        ReqlProto.Datum val= m_data.get(key);

        return val == null ? defaultValue : DatumConverter.getDouble(val);
    }

    public boolean getBool(String key) {
        return DatumConverter.getBool(m_data.get(key));
    }

    public boolean getBool(String key, boolean defaultValue) {
        ReqlProto.Datum val= m_data.get(key);

        return val == null ? defaultValue : DatumConverter.getBool(val);
    }

    public String getString(String key) {
        return DatumConverter.getString(m_data.get(key));
    }

    public String getString(String key, String defaultValue) {
        ReqlProto.Datum val= m_data.get(key);

        return val == null ? defaultValue : DatumConverter.getString(val);
    }

    public boolean isNull(String key) {
        return DatumConverter.isNull(m_data.get(key));
    }

    public boolean hasField(String key) {
        return m_data.containsKey(key);
    }

    // array
    public int[] getIntArray(String key) {
        return DatumConverter.getIntArray(m_data.get(key));
    }

    public long[] getLongArray(String key) {
        return DatumConverter.getLongArray(m_data.get(key));
    }

    public double[] getDoubleArray(String key) {
        return DatumConverter.getDoubleArray(m_data.get(key));

    }

    public boolean[] getBoolArray(String key) {
        return DatumConverter.getBoolArray(m_data.get(key));

    }

    public String[] getStringArray(String key) {
        return DatumConverter.getStringArray(m_data.get(key));

    }

    public JsonObject[] getObjArray(String key) {
        return DatumConverter.getObjArray(m_data.get(key));
    }

    public Object[] getArray(String key) {
        return DatumConverter.getArray(m_data.get(key));
    }

    // objects
    public JsonObject getObj(String key) {
        return new JsonObject(m_data.get(key).getRObjectList());
    }

    public <T> T get(String key) {
        return DatumConverter.get(m_data.get(key));
    }

    public <T> T get(String key, T defaultValue) {
        ReqlProto.Datum val= m_data.get(key);

        return val == null ? defaultValue : DatumConverter.<T>get(val);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result= new HashMap<String, Object>();
        for(Map.Entry<String, ReqlProto.Datum> e: m_data.entrySet()) {
            result.put(e.getKey(), DatumConverter.deepConvert(e.getValue()));
        }

        return result;
    }
}
