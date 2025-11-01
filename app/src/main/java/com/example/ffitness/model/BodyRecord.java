package com.example.ffitness.model;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BodyRecord {
    @SerializedName("_id")
    private String id;

    @SerializedName("height")
    private String height;

    @SerializedName("weight")
    private String weight;

    @SerializedName("bmi")
    private String bmi;

    @SerializedName("user")
    private String user;

    @SerializedName("bodyClassification")
    private String bodyClassification;
}
