package com.example.ffitness.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class History implements Serializable {
    @SerializedName("_id")
    private String id;

    @SerializedName("user")
    private User user;

    @SerializedName("workout")
    private Workout workout;

    @SerializedName("time")
    private long time;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;
}
