package com.example.ffitness.api;


import com.example.ffitness.dto.response.ExerciseResponse;
import com.example.ffitness.dto.response.WorkoutResponse;
import com.example.ffitness.model.Exercise;
import com.example.ffitness.model.Workout;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiClient {
    @GET("/api/exercises")
    Call<ApiResponse<ExerciseResponse>> getExercises();

    @GET("/api/exercises/{id}")
    Call<ApiResponse<Exercise>> getExerciseById(@Path("id") String id);

    @GET("/api/workouts")
    Call<ApiResponse<List<Workout>>> getWorkouts();
    
    @GET("/api/workouts/{id}")
    Call<ApiResponse<WorkoutResponse>> getWorkoutById(@Path("id") String id);
}
