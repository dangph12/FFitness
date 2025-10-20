package com.example.ffitness.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ffitness.R;

public class WorkoutFragment extends Fragment {

    private RecyclerView recyclerView;
    private WorkoutAdapter workoutAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        recyclerView = view.findViewById(R.id.recyclerView_workouts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // TODO: Setup adapter with data
        // workoutAdapter = new WorkoutAdapter(workoutList, workout -> {
        //     // Navigate to WorkoutDetailFragment
        //     WorkoutDetailFragment detailFragment = WorkoutDetailFragment.newInstance(workout.getId());
        //     MainActivity mainActivity = (MainActivity) getActivity();
        //     if (mainActivity != null) {
        //         mainActivity.navigateToFragment(detailFragment, true);
        //     }
        // });
        // recyclerView.setAdapter(workoutAdapter);
    }
}
