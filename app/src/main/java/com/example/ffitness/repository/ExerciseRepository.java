package com.example.ffitness.repository;

import android.app.Application;
import com.example.ffitness.api.ApiResponse;
import com.example.ffitness.api.ApiService;
import com.example.ffitness.dto.response.ExerciseResponse;
import com.example.ffitness.model.Exercise;

import retrofit2.Call;

public class ExerciseRepository {
    private final Application application;

    public ExerciseRepository(Application application) {
        this.application = application;
    }

    public Call<ApiResponse<ExerciseResponse>> getExercises() {
        return ApiService.getInstance(application).getApiClient().getExercises();
    }

    public Call<ApiResponse<Exercise>> getExerciseById(String id) {
        return ApiService.getInstance(application).getApiClient().getExerciseById(id);
    }
}