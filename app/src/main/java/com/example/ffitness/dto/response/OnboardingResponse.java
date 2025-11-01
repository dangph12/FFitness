package com.example.ffitness.dto.response;

import com.example.ffitness.model.BodyRecord;
import com.example.ffitness.model.Goal;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingResponse {
    @SerializedName("user")
    private String user;

    @SerializedName("bodyRecord")
    private BodyRecord bodyRecord;

    @SerializedName("goal")
    private Goal goal;
}
