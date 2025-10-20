package com.example.ffitness.ui;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class WorkoutViewHolder extends RecyclerView.ViewHolder {
        public WorkoutViewHolder() {
            super(null);
        }
    }
}
