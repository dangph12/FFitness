package com.example.ffitness.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ffitness.api.ApiResponse;
import com.example.ffitness.api.ApiService;
import com.example.ffitness.dto.response.AuthResponse;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private static final String TAG = "AuthRepository";
    private final Application application;

    public AuthRepository(Application application) {
        this.application = application;
    }

    public void login(String email, String password, LoginCallback callback) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email);
            jsonBody.put("password", password);

            RequestBody body = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.get("application/json")
            );

            Call<ApiResponse<AuthResponse>> call = ApiService.getInstance(application).getApiClient().login(body);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse<AuthResponse>> call, @NonNull Response<ApiResponse<AuthResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<AuthResponse> apiResponse = response.body();

                        if (apiResponse.getData() != null && apiResponse.getData().getAccessToken() != null) {
                            String accessToken = apiResponse.getData().getAccessToken();
                            Log.d(TAG, "Login successful");
                            callback.onSuccess(accessToken);
                        } else {
                            callback.onError("Invalid response: No access token");
                        }
                    } else {
                        String errorMsg = "Login failed";
                        try {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                JSONObject errorJson = new JSONObject(errorBody);
                                if (errorJson.has("message")) {
                                    errorMsg = errorJson.getString("message");
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error response", e);
                        }
                        callback.onError(errorMsg);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse<AuthResponse>> call, @NonNull Throwable t) {
                    Log.e(TAG, "Login error: " + t.getMessage(), t);
                    callback.onError("Network error: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error creating request: " + e.getMessage(), e);
            callback.onError("Error: " + e.getMessage());
        }
    }

    public interface LoginCallback {
        void onSuccess(String accessToken);

        void onError(String errorMessage);
    }
}
