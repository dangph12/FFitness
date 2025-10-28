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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.ffitness.R;
import com.example.ffitness.model.Exercise;
import com.example.ffitness.util.InstructionUtils;

public class ExerciseDetailFragment extends Fragment {

    private static final String TAG = "ExerciseDetailFragment";

    private TextView textViewExerciseTitle;
    private TextView textViewExerciseDescription;
    private ImageView imageViewExercise;

    public static ExerciseDetailFragment newInstance(Exercise exercise) {
        ExerciseDetailFragment fragment = new ExerciseDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("exercise", exercise);
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

        Bundle args = getArguments();
        if (args != null) {
            Exercise exercise = (Exercise) args.getSerializable("exercise");
            if (exercise != null) {
                Log.d(TAG, "Loaded exercise: " + exercise.getTitle());
                bindExerciseData(exercise);
            }
        }
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
