package com.yuluassignment.misc;

import android.content.Context;
import android.content.SharedPreferences;
import com.yuluassignment.MyApp;

public class SharedPrefs {

    public static void put(String key, String data) {

        getSharePrefs().edit().putString(key, data).commit();

    }

    public static void put(String key, long data) {

        getSharePrefs().edit().putLong(key, data).commit();

    }

    public static void put(String key, float data) {
        getSharePrefs().edit().putFloat(key, data).apply();
    }

    public static void put(String key, boolean data) {
        getSharePrefs().edit().putBoolean(key, data).apply();
    }

    public static String get(String key, String def) {

        return getSharePrefs().getString(key, def);

    }

    public static float get(String key) {
        return get(key, -1f);
    }

    public static float get(String key, float def) {
        return getSharePrefs().getFloat(key, def);
    }

    public static boolean get(String key, boolean def) {
        return getSharePrefs().getBoolean(key, def);
    }

    public static long getLong(String key, long def) {
        return getSharePrefs().getLong(key, def);
    }


    public static void removeValue(String key) {
        getSharePrefs().edit().remove(key).apply();
    }

    private static SharedPreferences getSharePrefs() {
        return MyApp.get().getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

}
