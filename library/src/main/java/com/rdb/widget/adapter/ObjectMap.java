package com.rdb.widget.adapter;

import java.util.HashMap;
import java.util.Map;

public class ObjectMap {

    private Map<String, Long> longs;
    private Map<String, Float> floats;
    private Map<String, Integer> ints;
    private Map<String, String> strings;
    private Map<String, Object> objects;
    private Map<String, Boolean> booleans;


    public void putObject(String key, Object value) {
        if (value != null) {
            if (objects == null) {
                objects = new HashMap<>();
            }
            objects.put(key, value);
        }
    }

    public void putString(String key, String value) {
        if (strings == null) {
            strings = new HashMap<>();
        }
        strings.put(key, value);
    }

    public void putInt(String key, int value) {
        if (ints == null) {
            ints = new HashMap<>();
        }
        ints.put(key, value);
    }

    public void putLong(String key, long value) {
        if (longs == null) {
            longs = new HashMap<>();
        }
        longs.put(key, value);
    }

    public void putFloat(String key, float value) {
        if (floats == null) {
            floats = new HashMap<>();
        }
        floats.put(key, value);
    }

    public void putBoolean(String key, boolean value) {
        if (booleans == null) {
            booleans = new HashMap<>();
        }
        booleans.put(key, value);
    }

    public Object getObject(String key) {
        if (objects != null && objects.containsKey(key)) {
            return objects.get(key);
        } else {
            return null;
        }
    }

    public String getString(String key, String defValue) {
        if (strings != null && strings.containsKey(key)) {
            return strings.get(key);
        } else {
            return defValue;
        }
    }

    public boolean getBoolean(String key, boolean defValue) {
        if (booleans != null && booleans.containsKey(key)) {
            return booleans.get(key);
        } else {
            return defValue;
        }
    }

    public int getInt(String key, int defValue) {
        if (ints != null && ints.containsKey(key)) {
            return ints.get(key);
        } else {
            return defValue;
        }
    }

    public long getLong(String key, long defValue) {
        if (longs != null && longs.containsKey(key)) {
            return longs.get(key);
        } else {
            return defValue;
        }
    }

    public float getFloat(String key, float defValue) {
        if (floats != null && floats.containsKey(key)) {
            return floats.get(key);
        } else {
            return defValue;
        }
    }
}
