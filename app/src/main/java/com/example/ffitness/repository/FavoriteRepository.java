package com.example.ffitness.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ffitness.api.ApiResponse;
import com.example.ffitness.api.ApiService;
import com.example.ffitness.dto.request.FavoriteRequest;
import com.example.ffitness.dto.response.FavoriteResponse;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteRepository {

    private static final String TAG = "FavoriteRepository";
    private final ApiService apiService;

    public FavoriteRepository(Application application) {
        this.apiService = ApiService.getInstance(application);
    }

    public void getFavoritesByUserId(String userId, int page, int limit, FavoritesCallback callback) {
        apiService.getApiClient().getFavoritesByUserId(userId, page, limit)
                .enqueue(new Callback<ApiResponse<FavoriteResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<FavoriteResponse>> call,
                                           @NonNull Response<ApiResponse<FavoriteResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<FavoriteResponse> apiResponse = response.body();
                            if (apiResponse.getData() != null) {
                                FavoriteResponse favoriteResponse = apiResponse.getData();
                                
                                // Calculate currentPage and hasNextPage on client side 
                                // since backend doesn't provide them
                                favoriteResponse.calculatePaginationFields(page);
                                
                                callback.onSuccess(favoriteResponse);
                            } else {
                                callback.onError("Failed to load favorites: No data in response");
                            }
                        } else {
                            callback.onError("Failed to load favorites: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<FavoriteResponse>> call,
                                          @NonNull Throwable t) {
                        Log.e(TAG, "Error loading favorites", t);
                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }

    public void addFavorite(String userId, String workoutId, FavoriteActionCallback callback) {
        FavoriteRequest favoriteRequest = new FavoriteRequest(userId, workoutId);
        
        String json = new Gson().toJson(favoriteRequest);
        RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json"));

        apiService.getApiClient().addFavorite(requestBody)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<Void>> call,
                                           @NonNull Response<ApiResponse<Void>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "Favorite added successfully");
                            callback.onSuccess();
                        } else {
                            callback.onError("Failed to add favorite: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<Void>> call,
                                          @NonNull Throwable t) {
                        Log.e(TAG, "Error adding favorite", t);
                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }

    public void removeFavorite(String favoriteId, FavoriteActionCallback callback) {
        apiService.getApiClient().removeFavorite(favoriteId)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<Void>> call,
                                           @NonNull Response<ApiResponse<Void>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "Favorite removed successfully");
                            callback.onSuccess();
                        } else {
                            callback.onError("Failed to remove favorite: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<Void>> call,
                                          @NonNull Throwable t) {
                        Log.e(TAG, "Error removing favorite", t);
                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }

    public interface FavoritesCallback {
        void onSuccess(FavoriteResponse favoriteResponse);
        void onError(String errorMessage);
    }

    public interface FavoriteActionCallback {
        void onSuccess();
        void onError(String errorMessage);
    }
}
