package com.example.ffitness.util;

import android.util.Base64;

import org.json.JSONObject;

public class JwtDecoder {
    
    public static String getUserIdFromToken(String token) {
        try {
            // JWT format: header.payload.signature
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return null;
            }
            
            // Decode the payload (second part)
            String payload = parts[1];
            
            // Use DEFAULT flag which handles padding automatically
            byte[] decodedBytes = Base64.decode(payload, Base64.DEFAULT);
            String decodedString = new String(decodedBytes, "UTF-8");
            
            // Parse JSON to get user ID
            JSONObject jsonObject = new JSONObject(decodedString);
            
            // Try common JWT claim names for user ID
            if (jsonObject.has("userId")) {
                return jsonObject.getString("userId");
            } else if (jsonObject.has("sub")) {
                return jsonObject.getString("sub");
            } else if (jsonObject.has("id")) {
                return jsonObject.getString("id");
            } else if (jsonObject.has("_id")) {
                return jsonObject.getString("_id");
            }
            
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
