package com.example.ffitness.api;


import com.example.ffitness.dto.response.ExerciseResponse;
import com.example.ffitness.dto.response.WorkoutResponse;
import com.example.ffitness.model.Exercise;
import com.example.ffitness.model.Workout;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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
    Call<ApiResponse<Workout>> getWorkoutById(@Path("id") String id);
    
    @POST("/api/auth/login")
    Call<ApiResponse<com.example.ffitness.dto.response.AuthResponse>> login(@Body RequestBody body);
}
