package com.example.ffitness.ui;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        public ExerciseViewHolder() {
            super(null);
        }
    }
}
