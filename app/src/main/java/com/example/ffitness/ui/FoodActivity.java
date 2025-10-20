package com.example.ffitness.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ffitness.R;
import com.example.ffitness.api.ApiResponse;
import com.example.ffitness.dto.response.FoodResponse;
import com.example.ffitness.model.Food;
import com.example.ffitness.repository.FoodRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodActivity extends AppCompatActivity {

    private static final String TAG = "FoodActivity";
    private FoodRepository foodRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        foodRepository = new FoodRepository(getApplication());
        
        // Fetch all foods
        fetchAllFoods();
        
        // Fetch single food by ID
        fetchSingleFood("68de1eca0ac8cfd1a7f68780");
    }

    private void fetchAllFoods() {
        Call<ApiResponse<FoodResponse>> call = foodRepository.getFoods();
        call.enqueue(new Callback<ApiResponse<FoodResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<FoodResponse>> call, Response<ApiResponse<FoodResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<FoodResponse> apiResponse = response.body();

                    Log.d(TAG, "Status: " + apiResponse.getStatus());
                    Log.d(TAG, "Message: " + apiResponse.getMessage());
                    Log.d(TAG, "==========================================");

                    if (apiResponse.getData() != null) {
                        FoodResponse foodResponse = apiResponse.getData();
                        Log.d(TAG, "Total foods: " + foodResponse.getTotalFoods());
                        Log.d(TAG, "Total pages: " + foodResponse.getTotalPages());
                        Log.d(TAG, "---");

                        List<Food> foods = foodResponse.getFoods();
                        if (foods != null) {
                            for (Food food : foods) {
                                Log.d(TAG, food.toString());
                                Log.d(TAG, "---");
                            }
                        }
                    }

                    Log.d(TAG, "==========================================");
                    Log.d(TAG, "All foods fetched successfully!");
                } else {
                    Log.e(TAG, "Fetch all foods failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<FoodResponse>> call, Throwable t) {
                Log.e(TAG, "Fetch all foods error: " + t.getMessage(), t);
            }
        });
    }

    private void fetchSingleFood(String foodId) {
        Call<ApiResponse<Food>> call = foodRepository.getFoodById(foodId);
        call.enqueue(new Callback<ApiResponse<Food>>() {
            @Override
            public void onResponse(Call<ApiResponse<Food>> call, Response<ApiResponse<Food>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Food> apiResponse = response.body();

                    Log.d(TAG, "Status: " + apiResponse.getStatus());
                    Log.d(TAG, "Message: " + apiResponse.getMessage());
                    Log.d(TAG, "==========================================");

                    if (apiResponse.getData() != null) {
                        Food food = apiResponse.getData();
                        Log.d(TAG, "Single Food Details:");
                        Log.d(TAG, food.toString());
                    }

                    Log.d(TAG, "==========================================");
                    Log.d(TAG, "Single food fetched successfully!");
                } else {
                    Log.e(TAG, "Fetch single food failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Food>> call, Throwable t) {
                Log.e(TAG, "Fetch single food error: " + t.getMessage(), t);
            }
        });
    }
}