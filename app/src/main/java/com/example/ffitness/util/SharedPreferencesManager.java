package com.example.ffitness.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "auth_prefs"; // Match ApiService
    private static final String KEY_ACCESS_TOKEN = "AUTH_TOKEN"; // Match ApiService
    private static final String KEY_USER_ID = "user_id";
    
    private final SharedPreferences sharedPreferences;
    
    public SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public void saveAccessToken(String token) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).apply();
    }
    
    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }
    
    public void saveUserId(String userId) {
        sharedPreferences.edit().putString(KEY_USER_ID, userId).apply();
    }
    
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }
    
    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
    
    public boolean isLoggedIn() {
        return getAccessToken() != null && getUserId() != null;
    }
}
