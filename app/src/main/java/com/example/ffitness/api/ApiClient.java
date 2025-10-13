package com.example.ffitness.api;


import com.example.ffitness.dto.response.FoodResponse;
import com.example.ffitness.model.Food;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiClient {
    @GET("/api/foods")
    Call<ApiResponse<FoodResponse>> getFoods();

    @GET("/api/foods/{id}")
    Call<ApiResponse<Food>> getFoodById(@Path("id") String id);
}
