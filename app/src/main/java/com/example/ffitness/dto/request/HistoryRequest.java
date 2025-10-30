package com.example.ffitness.dto.request;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryRequest {
    @SerializedName("user")
    private String user;

    @SerializedName("workout")
    private String workout;

    @SerializedName("time")
    private long time; // time in seconds or milliseconds
}
