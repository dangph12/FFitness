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
import com.example.ffitness.model.Favorite;
import com.example.ffitness.model.Workout;
import com.example.ffitness.repository.FavoriteRepository;
import com.example.ffitness.util.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoriteFragment extends Fragment {

    private static final String TAG = "FavoriteFragment";
    private static final int PAGE_SIZE = 10;

    private RecyclerView recyclerView;
    private WorkoutAdapter workoutAdapter;
    private TextView textEmpty;

    private FavoriteRepository favoriteRepository;
    private SharedPreferencesManager prefsManager;
    private LinearLayoutManager layoutManager;

    private int currentPage = 1;
    private int totalPages = 1;
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        recyclerView = view.findViewById(R.id.recycler_view_favorites);
        textEmpty = view.findViewById(R.id.text_empty);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

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
                    if (favoriteId != null) {
                        removeFavorite(favoriteId, position);
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
                        loadFavorites(currentPage + 1);
                    }
                }
            }
        });

        loadFavorites(1);
    }

    private void loadFavorites(int page) {
        String userId = prefsManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        isLoading = true;

        favoriteRepository.getFavoritesByUserId(userId, page, PAGE_SIZE, new FavoriteRepository.FavoritesCallback() {
            @Override
            public void onSuccess(FavoriteResponse response) {
                isLoading = false;
                totalPages = response.getTotalPages();
                currentPage = page;

                List<Favorite> favorites = response.getFavorites();
                favorites.sort((f1, f2) -> f2.getCreatedAt().compareTo(f1.getCreatedAt()));

                List<Workout> workouts = new ArrayList<>();
                Map<String, String> workoutToFavoriteMap = new HashMap<>();

                for (Favorite favorite : favorites) {
                    if (favorite.getWorkout() != null) {
                        workouts.add(favorite.getWorkout());
                        if (favorite.getWorkout().getId() != null) {
                            workoutToFavoriteMap.put(favorite.getWorkout().getId(), favorite.getId());
                        }
                    }
                }

                if (page == 1) {
                    workoutAdapter.setWorkouts(workouts);
                    workoutAdapter.setFavoriteMapping(workoutToFavoriteMap);

                    if (workouts.isEmpty()) {
                        textEmpty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        textEmpty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    // For pagination, add new workouts and update mappings
                    workoutAdapter.addWorkouts(workouts);
                    workoutAdapter.updateFavoriteMapping(workoutToFavoriteMap);
                }
            }

            @Override
            public void onError(String errorMessage) {
                isLoading = false;
                Log.e(TAG, "Failed to load favorites: " + errorMessage);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFavorite(String favoriteId, int position) {
        favoriteRepository.removeFavorite(favoriteId, new FavoriteRepository.FavoriteRemoveCallback() {
            @Override
            public void onSuccess() {
                workoutAdapter.removeWorkout(position);

                Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();

                if (workoutAdapter.getItemCount() == 0) {
                    textEmpty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Failed to remove favorite: " + errorMessage);
                Toast.makeText(getContext(), "Failed to remove from favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }
}