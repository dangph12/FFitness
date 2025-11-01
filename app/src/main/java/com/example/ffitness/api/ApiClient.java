package com.example.ffitness.api;


import com.example.ffitness.dto.response.AuthResponse;
import com.example.ffitness.dto.response.ExerciseResponse;
import com.example.ffitness.dto.response.FavoriteResponse;
import com.example.ffitness.dto.response.HistoryResponse;
import com.example.ffitness.dto.response.WorkoutResponse;
import com.example.ffitness.model.Exercise;
import com.example.ffitness.model.Favorite;
import com.example.ffitness.model.History;
import com.example.ffitness.model.Workout;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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
    Call<ApiResponse<AuthResponse>> login(@Body RequestBody body);

    @POST("/api/histories")
    Call<ApiResponse<History>> saveHistory(@Body RequestBody body);

    @GET("/api/histories/user/{userId}")
    Call<ApiResponse<HistoryResponse>> getHistoriesByUserId(
            @Path("userId") String userId,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("/api/favorites/user/{userId}")
    Call<ApiResponse<FavoriteResponse>> getFavoritesByUserId(
            @Path("userId") String userId,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @POST("/api/favorites")
    Call<ApiResponse<Favorite>> addFavorite(@Body RequestBody body);

    @DELETE("/api/favorites/{favoriteId}")
    Call<ApiResponse<Void>> removeFavorite(@Path("favoriteId") String favoriteId);

    @PUT("/api/users/onboarding")
    Call<ApiResponse<Void>> completeOnboarding(@Body RequestBody body);
}
