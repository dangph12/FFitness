package com.example.ffitness.dto.request;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingRequest {
    @SerializedName("userId")
    private String userId;

    @SerializedName("gender")
    private String gender;

    @SerializedName("dob")
    private String dob;

    @SerializedName("height")
    private double height;

    @SerializedName("weight")
    private double weight;

    @SerializedName("bmi")
    private double bmi;

    @SerializedName("targetWeight")
    private double targetWeight;

    @SerializedName("diet")
    private String diet;

    @SerializedName("fitnessGoal")
    private String fitnessGoal;
}
