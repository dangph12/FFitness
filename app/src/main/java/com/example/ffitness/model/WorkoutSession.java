package com.example.ffitness.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSession implements Serializable {
    @SerializedName("_id")
    private String id;

    @SerializedName("exercise")
    private Exercise exercise;

    @SerializedName("sets")
    private int[] sets;
}
