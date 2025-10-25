package com.example.ffitness.util;

import android.util.Base64;

import org.json.JSONObject;

public class JwtDecoder {
    
    public static String getUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return null;
            }
            
            String payload = parts[1];
            byte[] decodedBytes = Base64.decode(payload, Base64.DEFAULT);
            String decodedString = new String(decodedBytes, "UTF-8");
            JSONObject jsonObject = new JSONObject(decodedString);
            
            if (jsonObject.has("id")) {
                return jsonObject.getString("id");
            }
            
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
