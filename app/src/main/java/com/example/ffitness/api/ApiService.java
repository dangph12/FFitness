package com.example.ffitness.api;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {
    // base url for localhost in android emulator
    private static final String BASE_URL = "http://10.0.2.2:8000/";
    private static ApiService instance;
    private final Retrofit retrofit;
    private final ApiClient apiClient;

    private ApiService(Context context) {

        Interceptor authInterceptor = chain -> {
            SharedPreferences sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
            String authToken = sharedPreferences.getString("AUTH_TOKEN", null);

            Request originalRequest = chain.request();
            Request.Builder builder = originalRequest.newBuilder();

            if (authToken != null && !authToken.isEmpty()) {
                builder.header("Authorization", "Bearer " + authToken);
            }

            Request newRequest = builder.build();
            return chain.proceed(newRequest);
        };

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiClient = retrofit.create(ApiClient.class);
    }

    public static synchronized ApiService getInstance(Context context) {
        if (instance == null) {
            instance = new ApiService(context.getApplicationContext());
        }
        return instance;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }
}