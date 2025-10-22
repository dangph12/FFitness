package com.example.ffitness.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ffitness.R;

public class WorkoutDetailFragment extends Fragment {

    private TextView textViewWorkoutTitle;
    private RecyclerView recyclerViewExercises;
    private Button buttonStartWorkout;
    private ExerciseAdapter exerciseAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        textViewWorkoutTitle = view.findViewById(R.id.text_view_workout_title);
        recyclerViewExercises = view.findViewById(R.id.recycler_view_exercises);
        buttonStartWorkout = view.findViewById(R.id.button_start_workout);
        
        recyclerViewExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // TODO: Get workout ID from arguments and load workout details
        // Bundle args = getArguments();
        // if (args != null) {
        //     String workoutId = args.getString("workout_id");
        //     loadWorkoutDetails(workoutId);
        // }
        
        buttonStartWorkout.setOnClickListener(v -> {
            // Navigate to WorkoutSessionActivity
            // Intent intent = new Intent(getActivity(), WorkoutSessionActivity.class);
            // intent.putExtra("workout_id", workoutId);
            // startActivity(intent);
        });
    }
    
    public static WorkoutDetailFragment newInstance(String workoutId) {
        WorkoutDetailFragment fragment = new WorkoutDetailFragment();
        Bundle args = new Bundle();
        args.putString("workout_id", workoutId);
        fragment.setArguments(args);
        return fragment;
    }
}
