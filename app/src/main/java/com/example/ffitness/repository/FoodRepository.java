package com.example.ffitness.repository;

import com.example.ffitness.api.ApiResponse;
import com.example.ffitness.api.ApiService;
import com.example.ffitness.dto.response.FoodResponse;
import com.example.ffitness.model.Food;

import retrofit2.Call;

public class FoodRepository {

    public Call<ApiResponse<FoodResponse>> getFoods() {
        return ApiService.getInstance().getApiClient().getFoods();
    }

    public Call<ApiResponse<Food>> getFoodById(String id) {
        return ApiService.getInstance().getApiClient().getFoodById(id);
    }
}
