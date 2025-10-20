package com.example.ffitness.dto.response;

import com.example.ffitness.model.Exercise;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseResponse {
    @SerializedName("exercises")
    private List<Exercise> exercises;

    @SerializedName("totalExercises")
    private int totalExercises;

    @SerializedName("totalPages")
    private int totalPages;
}
