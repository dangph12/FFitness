package com.example.ffitness.ui;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExerciseSessionAdapter extends RecyclerView.Adapter<ExerciseSessionAdapter.ExerciseSessionViewHolder> {

    @NonNull
    @Override
    public ExerciseSessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseSessionViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ExerciseSessionViewHolder extends RecyclerView.ViewHolder {
        public ExerciseSessionViewHolder() {
            super(null);
        }
    }
}
