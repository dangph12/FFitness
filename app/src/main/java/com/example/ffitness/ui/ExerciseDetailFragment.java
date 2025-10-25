package com.example.ffitness.ui;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.ffitness.R;
import com.example.ffitness.model.Exercise;
import com.example.ffitness.repository.ExerciseRepository;
import com.example.ffitness.util.InstructionUtils;

public class ExerciseDetailFragment extends Fragment {

    private static final String TAG = "ExerciseDetailFragment";

    private TextView textViewExerciseTitle;
    private TextView textViewExerciseDescription;
    private ImageView imageViewExercise;

    private ExerciseRepository exerciseRepository;
    private String exerciseId;

    public static ExerciseDetailFragment newInstance(String exerciseId) {
        ExerciseDetailFragment fragment = new ExerciseDetailFragment();
        Bundle args = new Bundle();
        args.putString("exercise_id", exerciseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_detail, container, false);
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

        textViewExerciseTitle = view.findViewById(R.id.textView_exercise_title);
        textViewExerciseDescription = view.findViewById(R.id.textView_exercise_description);
        imageViewExercise = view.findViewById(R.id.imageView_exercise);

        exerciseRepository = new ExerciseRepository(requireActivity().getApplication());

        Bundle args = getArguments();
        if (args != null) {
            exerciseId = args.getString("exercise_id");
            if (exerciseId != null) {
                loadExerciseDetails(exerciseId);
            }
        }
    }

    private void loadExerciseDetails(String exerciseId) {
        exerciseRepository.getExerciseById(exerciseId, new ExerciseRepository.ExerciseCallback() {
            @Override
            public void onSuccess(Exercise exercise) {
                Log.d(TAG, "Loaded exercise: " + exercise.getTitle());
                bindExerciseData(exercise);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Failed to load exercise: " + errorMessage);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindExerciseData(Exercise exercise) {
        textViewExerciseTitle.setText(exercise.getTitle());

        String raw = exercise.getInstructions() != null ? exercise.getInstructions() : "";
        String plain = InstructionUtils.normalizeInstructions(raw);
        textViewExerciseDescription.setText(plain);
        textViewExerciseDescription.setMovementMethod(LinkMovementMethod.getInstance());

        if (exercise.getTutorial() != null && !exercise.getTutorial().isEmpty()) {
            Glide.with(this)
                    .load(exercise.getTutorial())
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(imageViewExercise);
        } else {
            imageViewExercise.setImageResource(R.drawable.logo);
        }
    }
}
