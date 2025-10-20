package com.example.ffitness.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ffitness.R;

public class ExerciseDetailFragment extends Fragment {

    private TextView textViewExerciseTitle;
    private TextView textViewExerciseDescription;
    private ImageView imageViewExercise;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        textViewExerciseTitle = view.findViewById(R.id.textView_exercise_title);
        textViewExerciseDescription = view.findViewById(R.id.textView_exercise_description);
        imageViewExercise = view.findViewById(R.id.imageView_exercise);
        
        // TODO: Get exercise ID from arguments and load exercise details
        // Bundle args = getArguments();
        // if (args != null) {
        //     String exerciseId = args.getString("exercise_id");
        //     loadExerciseDetails(exerciseId);
        // }
    }
    
    public static ExerciseDetailFragment newInstance(String exerciseId) {
        ExerciseDetailFragment fragment = new ExerciseDetailFragment();
        Bundle args = new Bundle();
        args.putString("exercise_id", exerciseId);
        fragment.setArguments(args);
        return fragment;
    }
}
