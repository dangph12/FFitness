package com.example.ffitness.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ffitness.MainActivity;
import com.example.ffitness.R;
import com.example.ffitness.model.Workout;

public class WorkoutDetailFragment extends Fragment {

    private static final String TAG = "WorkoutDetailFragment";

    private TextView textViewWorkoutTitle, textViewCreator, textViewVisibility;
    private ExerciseAdapter exerciseAdapter;

    private Workout currentWorkout;

    public static WorkoutDetailFragment newInstance(Workout workout) {
        WorkoutDetailFragment fragment = new WorkoutDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("workout", workout);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout_detail, container, false);
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

        textViewWorkoutTitle = view.findViewById(R.id.text_view_workout_title);
        textViewCreator = view.findViewById(R.id.text_view_creator);
        textViewVisibility = view.findViewById(R.id.text_view_visibility);
        RecyclerView recyclerViewExercises = view.findViewById(R.id.recycler_view_exercises);
        Button buttonStartWorkout = view.findViewById(R.id.button_start_workout);

        recyclerViewExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        exerciseAdapter = new ExerciseAdapter(workoutSession -> {
            ExerciseDetailFragment detailFragment = ExerciseDetailFragment.newInstance(workoutSession.getExercise());
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.navigateToFragment(detailFragment, true);
            }
        });
        recyclerViewExercises.setAdapter(exerciseAdapter);

        if (getArguments() != null) {
            currentWorkout = (Workout) getArguments().getSerializable("workout");
            if (currentWorkout != null) {
                Log.d(TAG, "Loaded workout: " + currentWorkout.getTitle());
                bindWorkoutData(currentWorkout);
            }
        }

        buttonStartWorkout.setOnClickListener(v -> {
            if (currentWorkout != null) {
                Intent intent = new Intent(getActivity(), WorkoutSessionActivity.class);
                intent.putExtra("workout", currentWorkout);
                startActivity(intent);
            }
        });
    }

    private void bindWorkoutData(Workout workout) {
        textViewWorkoutTitle.setText(workout.getTitle());
        textViewCreator.setText(workout.getUser() != null
                ? "by " + workout.getUser().getName()
                : "Unknown");
        textViewVisibility.setText(workout.getIsPublic() ? "Public" : "Private");
        textViewVisibility.setBackgroundResource(
                workout.getIsPublic() ? R.drawable.bg_badge_public : R.drawable.bg_badge_private
        );

        if (workout.getExercises() != null && !workout.getExercises().isEmpty()) {
            Log.d(TAG, "Loaded " + workout.getExercises().size() + " exercises");
            exerciseAdapter.setExercises(workout.getExercises());
        } else {
            Log.w(TAG, "No exercises found");
        }
    }
}
