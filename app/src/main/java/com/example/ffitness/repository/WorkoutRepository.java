package com.example.ffitness.repository;

import android.app.Application;

import com.example.ffitness.api.ApiResponse;
import com.example.ffitness.api.ApiService;
import com.example.ffitness.dto.response.WorkoutResponse;
import com.example.ffitness.model.Exercise;
import com.example.ffitness.model.Workout;

import java.util.List;

import retrofit2.Call;

public class WorkoutRepository {
    private final Application application;

    public WorkoutRepository(Application application) {
        this.application = application;
    }

    public Call<ApiResponse<WorkoutResponse>> getWorkouts() {
        return ApiService.getInstance(application).getApiClient().getWorkouts();
    }
    public Call<ApiResponse<WorkoutResponse>> getWorkoutById(String id) {
        return ApiService.getInstance(application).getApiClient().getWorkoutById(id);
    }
}
