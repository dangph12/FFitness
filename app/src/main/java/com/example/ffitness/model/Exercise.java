package com.example.ffitness.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exercise implements Serializable {
    @SerializedName("_id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("difficulty")
    private String difficulty;

    @SerializedName("type")
    private String type;

    @SerializedName("tutorial")
    private String tutorial;

    @SerializedName("instructions")
    private String instructions;

    @SerializedName("muscles")
    private List<Muscle> muscles;

    @SerializedName("equipments")
    private List<Equipment> equipments;

    @SerializedName("createdAt")
    private Date createdAt;

    @SerializedName("updatedAt")
    private Date updatedAt;
}
