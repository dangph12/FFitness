package com.example.ffitness.dto.response;

import com.example.ffitness.model.Food;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FoodResponse {
    @SerializedName("foods")
    private List<Food> foods;

    @SerializedName("totalFoods")
    private int totalFoods;

    @SerializedName("totalPages")
    private int totalPages;

    public List<Food> getFoods() {
        return foods;
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
    }

    public int getTotalFoods() {
        return totalFoods;
    }

    public void setTotalFoods(int totalFoods) {
        this.totalFoods = totalFoods;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
