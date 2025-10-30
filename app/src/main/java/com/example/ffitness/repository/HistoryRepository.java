package com.example.ffitness.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ffitness.api.ApiResponse;
import com.example.ffitness.api.ApiService;
import com.example.ffitness.dto.request.HistoryRequest;
import com.example.ffitness.model.History;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryRepository {
    private static final String TAG = "HistoryRepository";
    private final ApiService apiService;

    public HistoryRepository(Application application) {
        this.apiService = ApiService.getInstance(application);
    }

    public void saveHistory(String userId, String workoutId, long timeInSeconds, HistoryActionCallback callback) {
        HistoryRequest request = new HistoryRequest(userId, workoutId, timeInSeconds);

        Gson gson = new Gson();
        String json = gson.toJson(request);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));

        Log.d(TAG, "Saving history: " + json);

        apiService.getApiClient().saveHistory(body).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<History>> call, @NonNull Response<ApiResponse<History>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "History saved successfully");
                    callback.onSuccess();
                } else {
                    String error = "Failed to save history: " + response.code();
                    Log.e(TAG, error);
                    callback.onError(error);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<History>> call, @NonNull Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e(TAG, error, t);
                callback.onError(error);
            }
        });
    }

    public interface HistoryActionCallback {
        void onSuccess();

        void onError(String errorMessage);
    }
}
