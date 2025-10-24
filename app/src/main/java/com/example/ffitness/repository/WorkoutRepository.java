package com.example.ffitness.repository;

import android.app.Application;
import android.util.Log;

import com.example.ffitness.api.ApiResponse;
import com.example.ffitness.api.ApiService;
import com.example.ffitness.dto.response.WorkoutResponse;
import com.example.ffitness.model.Workout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkoutRepository {
    private static final String TAG = "WorkoutRepository";
    private final Application application;

    public WorkoutRepository(Application application) {
        this.application = application;
    }

    public interface WorkoutsCallback {
        void onSuccess(WorkoutResponse workoutResponse);
        void onError(String errorMessage);
    }

    public interface WorkoutCallback {
        void onSuccess(Workout workout);
        void onError(String errorMessage);
    }

    public void getWorkouts(int page, int limit, WorkoutsCallback callback) {
        Call<ApiResponse<WorkoutResponse>> call = ApiService.getInstance(application)
                .getApiClient()
                .getWorkouts(page, limit);
        
        call.enqueue(new Callback<ApiResponse<WorkoutResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<WorkoutResponse>> call, Response<ApiResponse<WorkoutResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<WorkoutResponse> apiResponse = response.body();
                    
                    if (apiResponse.getData() != null) {
                        Log.d(TAG, "Loaded workouts page " + page);
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError("No data in response");
                    }
                } else {
                    callback.onError("Failed to load workouts: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<WorkoutResponse>> call, Throwable t) {
                Log.e(TAG, "Error loading workouts: " + t.getMessage(), t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getWorkoutById(String id, WorkoutCallback callback) {
        Call<ApiResponse<Workout>> call = ApiService.getInstance(application)
                .getApiClient()
                .getWorkoutById(id);
        
        call.enqueue(new Callback<ApiResponse<Workout>>() {
            @Override
            public void onResponse(Call<ApiResponse<Workout>> call, Response<ApiResponse<Workout>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Workout> apiResponse = response.body();
                    
                    if (apiResponse.getData() != null) {
                        Log.d(TAG, "Loaded workout: " + apiResponse.getData().getTitle());
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError("No data in response");
                    }
                } else {
                    callback.onError("Failed to load workout: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Workout>> call, Throwable t) {
                Log.e(TAG, "Error loading workout: " + t.getMessage(), t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}
