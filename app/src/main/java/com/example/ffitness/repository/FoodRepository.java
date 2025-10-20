package com.example.ffitness.repository;

import android.app.Application;
import com.example.ffitness.api.ApiResponse;
import com.example.ffitness.api.ApiService;
import com.example.ffitness.dto.response.FoodResponse;
import com.example.ffitness.model.Food;

import retrofit2.Call;

public class FoodRepository {
    private final Application application;

    public FoodRepository(Application application) {
        this.application = application;
    }

    public Call<ApiResponse<FoodResponse>> getFoods() {
        return ApiService.getInstance(application).getApiClient().getFoods();
    }

    public Call<ApiResponse<Food>> getFoodById(String id) {
        return ApiService.getInstance(application).getApiClient().getFoodById(id);
    }
}