package com.example.ffitness.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ffitness.MainActivity;
import com.example.ffitness.R;
import com.example.ffitness.model.Workout;
import com.example.ffitness.repository.WorkoutRepository;

public class WorkoutDetailFragment extends Fragment {

    private static final String TAG = "WorkoutDetailFragment";

    private ImageView imageWorkout;
    private TextView textViewWorkoutTitle, textViewCreator, textViewVisibility;
    private RecyclerView recyclerViewExercises;
    private Button buttonStartWorkout;
    private ExerciseAdapter exerciseAdapter;

    private WorkoutRepository workoutRepository;
    private String workoutId;
    private Workout currentWorkout;

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

        imageWorkout = view.findViewById(R.id.image_workout_detail);
        textViewWorkoutTitle = view.findViewById(R.id.text_view_workout_title);
        textViewCreator = view.findViewById(R.id.text_view_creator);
        textViewVisibility = view.findViewById(R.id.text_view_visibility);
        recyclerViewExercises = view.findViewById(R.id.recycler_view_exercises);
        buttonStartWorkout = view.findViewById(R.id.button_start_workout);

        recyclerViewExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        exerciseAdapter = new ExerciseAdapter(workoutSession -> {
            ExerciseDetailFragment detailFragment = ExerciseDetailFragment.newInstance(workoutSession.getExercise().getId());
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.navigateToFragment(detailFragment, true);
            }
        });
        recyclerViewExercises.setAdapter(exerciseAdapter);

        workoutRepository = new WorkoutRepository(requireActivity().getApplication());

        if (getArguments() != null) {
            workoutId = getArguments().getString("workout_id");
            Log.d(TAG, "workout_id = " + workoutId);
            if (workoutId != null) {
                loadWorkoutDetails(workoutId);
            }
        }

        buttonStartWorkout.setOnClickListener(v -> {
            if (currentWorkout != null) {
                Intent intent = new Intent(getActivity(), WorkoutSessionActivity.class);
                intent.putExtra("workout_id", workoutId);
                intent.putExtra("workout", currentWorkout);
                startActivity(intent);
            }
        });
    }

    private void loadWorkoutDetails(String workoutId) {
        workoutRepository.getWorkoutById(workoutId, new WorkoutRepository.WorkoutCallback() {
            @Override
            public void onSuccess(Workout workout) {
                Log.d(TAG, "Loaded workout: " + workout.getTitle());
                bindWorkoutData(workout);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Failed to load workout: " + errorMessage);
            }
        });
    }

    private void bindWorkoutData(Workout workout) {
        currentWorkout = workout;
        
        textViewWorkoutTitle.setText(workout.getTitle());
        textViewCreator.setText(workout.getUser() != null
                ? "by " + workout.getUser().getName()
                : "Unknown");
        textViewVisibility.setText(workout.getIsPublic() ? "Public" : "Private");
        textViewVisibility.setBackgroundResource(
                workout.getIsPublic() ? R.drawable.bg_badge_public : R.drawable.bg_badge_private
        );

        String imageUrl = workout.getImage();
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            if (workout.getExercises() != null && !workout.getExercises().isEmpty()) {
                imageUrl = workout.getExercises().get(0).getExercise().getTutorial();
            }
        }


        if (workout.getExercises() != null && !workout.getExercises().isEmpty()) {
            Log.d(TAG, "Loaded " + workout.getExercises().size() + " exercises");
            exerciseAdapter.setExercises(workout.getExercises());
        } else {
            Log.w(TAG, "No exercises found");
        }
    }

    public static WorkoutDetailFragment newInstance(String workoutId) {
        WorkoutDetailFragment fragment = new WorkoutDetailFragment();
        Bundle args = new Bundle();
        args.putString("workout_id", workoutId);
        fragment.setArguments(args);
        return fragment;
    }
}
