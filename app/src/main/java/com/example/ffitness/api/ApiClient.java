package com.example.ffitness.api;


import com.example.ffitness.dto.response.ExerciseResponse;
import com.example.ffitness.dto.response.WorkoutResponse;
import com.example.ffitness.model.Exercise;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiClient {
    @GET("/api/exercises")
    Call<ApiResponse<ExerciseResponse>> getExercises();

    @GET("/api/exercises/{id}")
    Call<ApiResponse<Exercise>> getExerciseById(@Path("id") String id);

    @GET("/api/workouts")
    Call<ApiResponse<WorkoutResponse>> getWorkouts(
            @Query("page") int page,
            @Query("limit") int limit
    );
    
    @GET("/api/workouts/{id}")
    Call<ApiResponse<WorkoutResponse>> getWorkoutById(@Path("id") String id);
}
