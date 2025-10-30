package com.example.ffitness.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.ffitness.R;
import com.example.ffitness.model.Exercise;
import com.example.ffitness.model.WorkoutSession;
import com.example.ffitness.util.InstructionUtils;

public class ExerciseSessionFragment extends Fragment {

    private TextView textExerciseTitle, textExerciseInfo, textExerciseInstructions;
    private RadioGroup radioGroupSets;
    private ImageView imageExerciseTutorial;

    private WorkoutSession workoutSession;
    private int exerciseIndex;
    private int totalExercises;
    private int currentSetIndex = 0;

    public static ExerciseSessionFragment newInstance(WorkoutSession workoutSession, int exerciseIndex, int totalExercises) {
        ExerciseSessionFragment fragment = new ExerciseSessionFragment();
        Bundle args = new Bundle();
        args.putSerializable("workout_session", workoutSession);
        args.putInt("exercise_index", exerciseIndex);
        args.putInt("total_exercises", totalExercises);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_session, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textExerciseTitle = view.findViewById(R.id.text_exercise_title);
        textExerciseInfo = view.findViewById(R.id.text_exercise_info);
        textExerciseInstructions = view.findViewById(R.id.text_exercise_instructions);
        radioGroupSets = view.findViewById(R.id.radio_group_sets);
        imageExerciseTutorial = view.findViewById(R.id.image_exercise_tutorial);

        if (getArguments() != null) {
            workoutSession = (WorkoutSession) getArguments().getSerializable("workout_session");
            exerciseIndex = getArguments().getInt("exercise_index", 0);
            totalExercises = getArguments().getInt("total_exercises", 1);

            if (workoutSession != null) {
                bindExerciseData();
            }
        }
    }

    private void bindExerciseData() {
        Exercise exercise = workoutSession.getExercise();

        textExerciseTitle.setText(exercise.getTitle());
        textExerciseInfo.setText("Exercise " + (exerciseIndex + 1) + " of " + totalExercises);

        String raw = exercise.getInstructions() != null ? exercise.getInstructions() : "";
        String plain = InstructionUtils.normalizeInstructions(raw);
        textExerciseInstructions.setText(plain);

        if (exercise.getTutorial() != null && !exercise.getTutorial().isEmpty()) {
            Glide.with(this)
                    .load(exercise.getTutorial())
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(imageExerciseTutorial);
        } else {
            imageExerciseTutorial.setImageResource(R.drawable.logo);
        }

        radioGroupSets.removeAllViews();
        int[] sets = workoutSession.getSets();
        if (sets != null && sets.length > 0) {
            for (int i = 0; i < sets.length; i++) {
                RadioButton radioButton = new RadioButton(getContext());
                radioButton.setText("Set " + (i + 1) + ": " + sets[i] + " reps");
                radioButton.setTextSize(16);
                radioButton.setPadding(16, 16, 16, 16);
                radioButton.setId(View.generateViewId());
                radioGroupSets.addView(radioButton);
            }

            if (radioGroupSets.getChildCount() > 0) {
                ((RadioButton) radioGroupSets.getChildAt(0)).setChecked(true);
            }
        }

        radioGroupSets.setOnCheckedChangeListener((group, checkedId) -> {
            for (int i = 0; i < group.getChildCount(); i++) {
                if (group.getChildAt(i).getId() == checkedId) {
                    currentSetIndex = i;
                    break;
                }
            }
        });
    }

    public int getTotalSets() {
        return workoutSession != null && workoutSession.getSets() != null
                ? workoutSession.getSets().length
                : 0;
    }

    public boolean isLastSet() {
        int totalSets = getTotalSets();
        return totalSets > 0 && currentSetIndex == totalSets;
    }

    public void markSetCompleted() {
        if (currentSetIndex < radioGroupSets.getChildCount()) {
            RadioButton currentRadio = (RadioButton) radioGroupSets.getChildAt(currentSetIndex);
            currentRadio.setEnabled(false);
            currentRadio.setAlpha(0.5f);

            if (currentSetIndex + 1 < radioGroupSets.getChildCount()) {
                RadioButton nextRadio = (RadioButton) radioGroupSets.getChildAt(currentSetIndex + 1);
                nextRadio.setChecked(true);
                currentSetIndex++;
            }
        }
    }
}
