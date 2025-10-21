package com.example.ffitness.dto.response;

import com.example.ffitness.model.Workout;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutResponse {

    @SerializedName("workouts")
    private List<Workout> workouts;

    @SerializedName("totalWorkouts")
    private int totalWorkouts;

    @SerializedName("totalPages")
    private int totalPages;
}
