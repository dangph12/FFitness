package com.example.ffitness;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ffitness.api.ApiResponse;
import com.example.ffitness.dto.response.ExerciseResponse;
import com.example.ffitness.repository.ExerciseRepository;
import com.example.ffitness.ui.HomeFragment;
import com.example.ffitness.ui.HistoryFragment;
import com.example.ffitness.ui.ProfileFragment;
import com.example.ffitness.ui.WorkoutFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = "MainActivity";
    private ExerciseRepository exerciseRepository;
    
    private BottomNavigationView bottomNavigationView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        exerciseRepository = new ExerciseRepository(getApplication());
        fetchExercises();
        
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
        
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.navigation_workout) {
                selectedFragment = new WorkoutFragment();
            } else if (itemId == R.id.navigation_history) {
                selectedFragment = new HistoryFragment();
            } else if (itemId == R.id.navigation_profile) {
                selectedFragment = new ProfileFragment();
            }
            
            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }
    
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
    
    public void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public void fetchExercises() {
        Log.d(TAG, "Fetching exercises...");
        
        exerciseRepository.getExercises().enqueue(new Callback<ApiResponse<ExerciseResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ExerciseResponse>> call, Response<ApiResponse<ExerciseResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ExerciseResponse> apiResponse = response.body();
                    Log.d(TAG, "Success: " + apiResponse.getMessage());
                    Log.d(TAG, "Status: " + apiResponse.getStatus());
                    
                    if (apiResponse.getData() != null) {
                        ExerciseResponse exerciseResponse = apiResponse.getData();
                        Log.d(TAG, "Total exercises: " + exerciseResponse.getTotalExercises());
                        Log.d(TAG, "Total pages: " + exerciseResponse.getTotalPages());
                        Log.d(TAG, "Exercises in current page: " + exerciseResponse.getExercises().size());
                        
                        // Log first exercise as sample
                        if (!exerciseResponse.getExercises().isEmpty()) {
                            Log.d(TAG, "First exercise: " + exerciseResponse.getExercises().get(0).getTitle());
                            Log.d(TAG, "Difficulty: " + exerciseResponse.getExercises().get(0).getDifficulty());
                            Log.d(TAG, "Type: " + exerciseResponse.getExercises().get(0).getType());
                        }
                    }
                } else {
                    Log.e(TAG, "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ExerciseResponse>> call, Throwable t) {
                Log.e(TAG, "Failed to fetch exercises: " + t.getMessage(), t);
            }
        });
    }
}