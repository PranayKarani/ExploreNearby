package com.yuluassignment.misc;

import android.content.Context;
import android.content.SharedPreferences;
import com.yuluassignment.MyApp;

public class SharedPrefs {

    public static void writeData(String key, String data) {

        getSharePrefs().edit().putString(key, data).commit();

    }

    public static void writeData(String key, long data) {

        getSharePrefs().edit().putLong(key, data).commit();

    }

    public static void writeData(String key, int data) {
        getSharePrefs().edit().putInt(key, data).apply();
    }

    public static void writeData(String key, boolean data) {
        getSharePrefs().edit().putBoolean(key, data).apply();
    }

    public static String readData(String key, String def) {

        return getSharePrefs().getString(key, def);

    }

    public static int readData(String key, int def) {
        return getSharePrefs().getInt(key, def);
    }

    public static boolean readData(String key, boolean def) {
        return getSharePrefs().getBoolean(key, def);
    }

    public static long readLongData(String key, long def) {
        return getSharePrefs().getLong(key, def);
    }


    public static void removeValue(String key) {
        getSharePrefs().edit().remove(key).apply();
    }

    private static SharedPreferences getSharePrefs() {
        return MyApp.get().getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

}
