package com.example.ffitness.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    @SerializedName("_id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("role")
    private String role;

    @SerializedName("dob")
    private Date dob;

    @SerializedName("gender")
    private String gender;

    @SerializedName("isActive")
    private Boolean isActive;

    @SerializedName("profileCompleted")
    private Boolean profileCompleted;
}
