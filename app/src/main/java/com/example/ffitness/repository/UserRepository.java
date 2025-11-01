package com.example.ffitness.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ffitness.api.ApiResponse;
import com.example.ffitness.api.ApiService;
import com.example.ffitness.dto.request.OnboardingRequest;
import com.example.ffitness.model.User;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private final ApiService apiService;

    public UserRepository(Application application) {
        this.apiService = ApiService.getInstance(application);
    }

    public void completeOnboarding(String userId, String gender, String dob,
                                   double height, double weight, double bmi,
                                   double targetWeight, String diet, String fitnessGoal,
                                   UserOnboardingCallback callback) {
        OnboardingRequest request = new OnboardingRequest(
                userId, gender, dob, height, weight, bmi, targetWeight, diet, fitnessGoal
        );

        Gson gson = new Gson();
        String json = gson.toJson(request);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));

        Log.d(TAG, "Complete onboarding: " + json);

        apiService.getApiClient().completeOnboarding(body).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Onboarding completed successfully");
                    callback.onSuccess();
                } else {
                    String error = "Failed to complete onboarding: " + response.code();
                    Log.e(TAG, error);
                    callback.onError(error);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e(TAG, error, t);
                callback.onError(error);
            }
        });
    }

    public interface UserOnboardingCallback {
        void onSuccess();
        void onError(String errorMessage);
    }
}