package com.example.ffitness.model;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goal {
    @SerializedName("_id")
    private String id;

    @SerializedName("targetWeight")
    private String targetWeight;

    @SerializedName("fitnessGoal")
    private String fitnessGoal;

    @SerializedName("user")
    private String user;

    @SerializedName("diet")
    private String diet;
}
