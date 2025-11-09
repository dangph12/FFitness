package com.example.ffitness.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ffitness.MainActivity;
import com.example.ffitness.R;
import com.example.ffitness.dto.response.FavoriteResponse;
import com.example.ffitness.dto.response.WorkoutResponse;
import com.example.ffitness.model.Favorite;
import com.example.ffitness.repository.FavoriteRepository;
import com.example.ffitness.repository.WorkoutRepository;
import com.example.ffitness.util.SharedPreferencesManager;

import java.util.HashMap;
import java.util.Map;

public class WorkoutFragment extends Fragment {

    private static final String TAG = "WorkoutFragment";
    private static final int PAGE_SIZE = 10;

    private WorkoutAdapter workoutAdapter;
    private WorkoutRepository workoutRepository;
    private FavoriteRepository favoriteRepository;
    private SharedPreferencesManager prefsManager;
    private LinearLayoutManager layoutManager;

    private int currentPage = 1;
    private int totalPages = 1;
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
                mainActivity.navigateToFragment(new HomeFragment(), false);
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_workouts);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        workoutRepository = new WorkoutRepository(requireActivity().getApplication());
        favoriteRepository = new FavoriteRepository(requireActivity().getApplication());
        prefsManager = new SharedPreferencesManager(requireContext());

        workoutAdapter = new WorkoutAdapter(
                workout -> {
                    WorkoutDetailFragment detailFragment = WorkoutDetailFragment.newInstance(workout);
                    MainActivity mainActivity = (MainActivity) getActivity();
                    if (mainActivity != null) {
                        mainActivity.navigateToFragment(detailFragment, true);
                    }
                },
                (workout, position, favoriteId) -> {
                    String userId = prefsManager.getUserId();
                    if (userId == null || userId.isEmpty()) {
                        return;
                    }

                    if (favoriteId != null) {
                        favoriteRepository.removeFavorite(favoriteId, new FavoriteRepository.FavoriteRemoveCallback() {
                            @Override
                            public void onSuccess() {
                                workoutAdapter.removeFavorite(workout.getId());
                                Log.d(TAG, "Favorite removed successfully");
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Log.e(TAG, "Failed to remove favorite: " + errorMessage);
                            }
                        });
                    } else {
                        favoriteRepository.addFavorite(userId, workout.getId(), new FavoriteRepository.FavoriteAddCallback() {
                            @Override
                            public void onSuccess(Favorite favorite) {
                                workoutAdapter.addFavorite(workout.getId(), favorite.getId());
                                Log.d(TAG, "Favorite added successfully");
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Log.e(TAG, "Failed to add favorite: " + errorMessage);
                            }
                        });
                    }
                }
        );
        recyclerView.setAdapter(workoutAdapter);

        // Infinite scroll listener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && currentPage < totalPages) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 2) {
                        loadWorkouts(currentPage + 1);
                    }
                }
            }
        });

        // Load favorites first, then workouts
        String userId = prefsManager.getUserId();
        if (userId != null && !userId.isEmpty()) {
            loadFavorites(userId);
            loadWorkouts(1);
        } else {
            loadWorkouts(1);
        }
    }

    private void loadFavorites(String userId) {
        // Load all favorites for the user by getting first 100 favorites
        favoriteRepository.getFavoritesByUserId(userId, 1, 100, new FavoriteRepository.FavoritesCallback() {
            @Override
            public void onSuccess(FavoriteResponse response) {
                Map<String, String> workoutToFavoriteMap = new HashMap<>();
                for (Favorite fav : response.getFavorites()) {
                    if (fav.getWorkout() != null && fav.getWorkout().getId() != null) {
                        workoutToFavoriteMap.put(fav.getWorkout().getId(), fav.getId());
                    }
                }
                workoutAdapter.setFavoriteMapping(workoutToFavoriteMap);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Failed to load favorites: " + errorMessage);
            }
        });
    }

    private void loadWorkouts(int page) {
        isLoading = true;
        Log.d(TAG, "Loading workouts page: " + page);

        workoutRepository.getWorkouts(page, PAGE_SIZE, new WorkoutRepository.WorkoutsCallback() {
            @Override
            public void onSuccess(WorkoutResponse workoutResponse) {
                isLoading = false;
                totalPages = workoutResponse.getTotalPages();
                currentPage = page;

                Log.d(TAG, "Loaded page " + page + " of " + totalPages);
                Log.d(TAG, "Workouts in page: " + workoutResponse.getWorkouts().size());

                if (page == 1) {
                    workoutAdapter.setWorkouts(workoutResponse.getWorkouts());
                } else {
                    workoutAdapter.addWorkouts(workoutResponse.getWorkouts());
                }
            }

            @Override
            public void onError(String errorMessage) {
                isLoading = false;
                Log.e(TAG, "Failed to load workouts: " + errorMessage);
            }
        });
    }

    private boolean isAtBottom(RecyclerView recyclerView) {
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

        return (visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - 2);
    }

    private void setLoading(boolean loading) {
        this.isLoading = loading;
        if (loading) {
            Log.d(TAG, "Đang tải thêm dữ liệu...");
        } else {
            Log.d(TAG, "Tải dữ liệu hoàn thành.");
        }
    }
    
    private void safeToast(String message) {
        if (getActivity() != null && isAdded()) {
            android.widget.Toast.makeText(getActivity(), message, android.widget.Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "Fragment not attached → toast skipped: " + message);
        }
    }
}