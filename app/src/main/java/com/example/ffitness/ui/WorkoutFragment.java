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
import com.example.ffitness.dto.response.WorkoutResponse;
import com.example.ffitness.repository.WorkoutRepository;

public class WorkoutFragment extends Fragment {

    private static final String TAG = "WorkoutFragment";
    private static final int PAGE_SIZE = 10;

    private RecyclerView recyclerView;
    private WorkoutAdapter workoutAdapter;
    private WorkoutRepository workoutRepository;
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
        
        recyclerView = view.findViewById(R.id.recycler_view_workouts);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        
        workoutRepository = new WorkoutRepository(requireActivity().getApplication());
        
        workoutAdapter = new WorkoutAdapter(workout -> {
            WorkoutDetailFragment detailFragment = WorkoutDetailFragment.newInstance(workout);
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.navigateToFragment(detailFragment, true);
            }
        });
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
        
        loadWorkouts(1);
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
}
