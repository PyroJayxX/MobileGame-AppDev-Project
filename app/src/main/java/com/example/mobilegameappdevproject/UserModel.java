package com.example.mobilegameappdevproject;

import android.content.Context;
import android.content.SharedPreferences;

public class UserModel {
    private static final String PREF_NAME = "user_pref";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_USERNAME = "username";

    static String email, password, username, confirmPassword;

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void saveUser(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public static void loadUser(Context context) {
        SharedPreferences prefs = getSharedPreferences(context);
        email = prefs.getString(KEY_EMAIL, null);
        password = prefs.getString(KEY_PASSWORD, null);
        username = prefs.getString(KEY_USERNAME, null);
    }

    public static void onUserLogout(Context context) {
        email = null;
        password = null;
        username = null;
        confirmPassword = null;
        // Clear SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.clear().apply();
    }
}
