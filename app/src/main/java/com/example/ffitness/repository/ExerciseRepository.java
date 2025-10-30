package com.example.ffitness.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ffitness.api.ApiResponse;
import com.example.ffitness.api.ApiService;
import com.example.ffitness.dto.response.ExerciseResponse;
import com.example.ffitness.model.Exercise;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseRepository {
    private static final String TAG = "ExerciseRepository";
    private final Application application;

    public ExerciseRepository(Application application) {
        this.application = application;
    }

    public void getExercises(ExercisesCallback callback) {
        Call<ApiResponse<ExerciseResponse>> call = ApiService.getInstance(application)
                .getApiClient()
                .getExercises();

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<ExerciseResponse>> call, Response<ApiResponse<ExerciseResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ExerciseResponse> apiResponse = response.body();

                    if (apiResponse.getData() != null) {
                        Log.d(TAG, "Loaded exercises successfully");
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError("No data in response");
                    }
                } else {
                    callback.onError("Failed to load exercises: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ExerciseResponse>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error loading exercises: " + t.getMessage(), t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getExerciseById(String id, ExerciseCallback callback) {
        Call<ApiResponse<Exercise>> call = ApiService.getInstance(application)
                .getApiClient()
                .getExerciseById(id);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Exercise>> call, Response<ApiResponse<Exercise>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Exercise> apiResponse = response.body();

                    if (apiResponse.getData() != null) {
                        Log.d(TAG, "Loaded exercise: " + apiResponse.getData().getTitle());
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError("No data in response");
                    }
                } else {
                    callback.onError("Failed to load exercise: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Exercise>> call, Throwable t) {
                Log.e(TAG, "Error loading exercise: " + t.getMessage(), t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public interface ExercisesCallback {
        void onSuccess(ExerciseResponse exerciseResponse);

        void onError(String errorMessage);
    }

    public interface ExerciseCallback {
        void onSuccess(Exercise exercise);

        void onError(String errorMessage);
    }
}