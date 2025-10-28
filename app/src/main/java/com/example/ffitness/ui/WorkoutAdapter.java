package com.example.ffitness.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ffitness.R;
import com.example.ffitness.model.Workout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private List<Workout> workouts;
    private final OnWorkoutClickListener listener;
    private final OnFavoriteClickListener favoriteListener;
    private final Set<String> favoriteWorkoutIds = new HashSet<>();

    public boolean isFavorite(String workoutId) {
        return workoutId != null && favoriteWorkoutIds.contains(workoutId);
    }

    public interface OnWorkoutClickListener {
        void onWorkoutClick(Workout workout);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Workout workout, int position);
    }

    public WorkoutAdapter(OnWorkoutClickListener listener, OnFavoriteClickListener favoriteListener) {
        this.workouts = new ArrayList<>();
        this.listener = listener;
        this.favoriteListener = favoriteListener;
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
        notifyDataSetChanged();
    }

    public void setFavoriteIds(Set<String> ids) {
        favoriteWorkoutIds.clear();
        if (ids != null) favoriteWorkoutIds.addAll(ids);
        notifyDataSetChanged();
    }

    public void addWorkouts(List<Workout> newWorkouts) {
        int oldSize = workouts.size();
        workouts.addAll(newWorkouts);
        notifyItemRangeInserted(oldSize, newWorkouts.size());
    }

    public void removeWorkout(int position) {
        if (position >= 0 && position < workouts.size()) {
            workouts.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_workout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = workouts.get(position);
        holder.bind(workout);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onWorkoutClick(workout);
            }
        });

        // Handle favorite button click if listener is provided
        if (holder.btnFavorite != null) {
            // Always show the favorite button in the row so the UI is consistent.
            // If a listener is provided, make it clickable. Otherwise show it
            // disabled (non-clickable) so the icon doesn't disappear unexpectedly.
            holder.btnFavorite.setVisibility(View.VISIBLE);
            if (favoriteListener != null) {
                holder.btnFavorite.setEnabled(true);
                holder.btnFavorite.setOnClickListener(v -> favoriteListener.onFavoriteClick(workout, position));
            } else {
                holder.btnFavorite.setEnabled(false);
                holder.btnFavorite.setOnClickListener(null);
            }

            // Update icon (filled or outline) based on favorite state
            boolean isFav = workout.getId() != null && favoriteWorkoutIds.contains(workout.getId());
            holder.btnFavorite.setImageResource(isFav ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
        }
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageWorkout;
        private final TextView textTitle;
        private final TextView textCreator;
        private final TextView textVisibility;
        private final TextView textDetails;
        private final ImageButton btnFavorite;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            imageWorkout = itemView.findViewById(R.id.image_workout);
            textTitle = itemView.findViewById(R.id.text_view_workout_title);
            textCreator = itemView.findViewById(R.id.text_view_workout_creator);
            textVisibility = itemView.findViewById(R.id.text_view_visibility);
            textDetails = itemView.findViewById(R.id.text_view_workout_details);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
        }

        public void bind(Workout workout) {
            textTitle.setText(workout.getTitle());
            textCreator.setText(workout.getUser() != null ? "by " + workout.getUser().getName() : "by Unknown");
            textVisibility.setText(workout.getIsPublic() ? "Public" : "Private");
            textVisibility.setBackgroundResource(
                    workout.getIsPublic() ? R.drawable.bg_badge_public : R.drawable.bg_badge_private
            );

            int exerciseCount = workout.getExercises() != null ? workout.getExercises().size() : 0;
            textDetails.setText(exerciseCount + " Exercises");

            String imageUrl = workout.getImage();

            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.logo)
                        .into(imageWorkout);
            } else {
                imageWorkout.setImageResource(R.drawable.logo);
            }
        }
    }
}
