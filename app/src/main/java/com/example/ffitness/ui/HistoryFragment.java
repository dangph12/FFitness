package com.example.ffitness.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ffitness.MainActivity;
import com.example.ffitness.R;
import com.example.ffitness.dto.response.FavoriteResponse;
import com.example.ffitness.dto.response.HistoryResponse;
import com.example.ffitness.model.Favorite;
import com.example.ffitness.model.History;
import com.example.ffitness.model.Workout;
import com.example.ffitness.repository.FavoriteRepository;
import com.example.ffitness.repository.HistoryRepository;
import com.example.ffitness.util.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryFragment extends Fragment {

    private static final String TAG = "HistoryFragment";
    private static final int PAGE_SIZE = 10;

    private RecyclerView recyclerView;
    private WorkoutAdapter workoutAdapter;
    private TextView textEmpty;

    private HistoryRepository historyRepository;

    private FavoriteRepository favoriteRepository;

    private SharedPreferencesManager prefsManager;
    private LinearLayoutManager layoutManager;

    private int currentPage = 1;
    private int totalPages = 1;
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            com.example.ffitness.MainActivity mainActivity = (com.example.ffitness.MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
                mainActivity.navigateToFragment(new HomeFragment(), false);
            }
        });

        recyclerView = view.findViewById(R.id.recycler_view_histories);
        textEmpty = view.findViewById(R.id.text_empty);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        historyRepository = new HistoryRepository(requireActivity().getApplication());
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
                        loadHistories(currentPage + 1);
                    }
                }
            }
        });

        // Load favorites first, then workouts
        String userId = prefsManager.getUserId();
        if (userId != null && !userId.isEmpty()) {
            loadFavorites(userId);
            loadHistories(1);
        } else {
            loadHistories(1);
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

    private void loadHistories(int page) {
        String userId = prefsManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        isLoading = true;
        Log.d(TAG, "Loading histories page: " + page);

        historyRepository.getHistoriesByUserId(userId, page, PAGE_SIZE, new HistoryRepository.HistoryCallback() {
            @Override
            public void onSuccess(HistoryResponse historyResponse) {
                isLoading = false;
                totalPages = historyResponse.getTotalPages();
                currentPage = page;

                List<Workout> workouts = new ArrayList<>();

                for (History history: historyResponse.getHistories()) {
                    if (history.getWorkout() != null) {
                        workouts.add(history.getWorkout());
                    }
                }

                if (page == 1) {
                    workoutAdapter.setWorkouts(workouts);
                } else {
                    workoutAdapter.addWorkouts(workouts);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Failed to load histories: " + errorMessage);
            }
        });
    }
}
