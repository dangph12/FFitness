package com.example.ffitness.dto.response;

import com.example.ffitness.model.Favorite;
import com.example.ffitness.model.History;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryResponse {

    @SerializedName("histories")
    private List<History> histories;

    @SerializedName("totalHistories")
    private int totalHistories;

    @SerializedName("totalPages")
    private int totalPages;

    // Calculated fields on client side (backend doesn't provide these)
    private int currentPage;
    private boolean hasNextPage;

    /**
     * Calculate currentPage and hasNextPage based on request parameters
     * Call this after receiving response from API
     * 
     * @param requestedPage The page number that was requested in the API call
     */
    public void calculatePaginationFields(int requestedPage) {
        this.currentPage = requestedPage;
        this.hasNextPage = currentPage < totalPages;
    }
}
