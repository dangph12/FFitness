package com.example.ffitness.ui;

import android.annotation.SuppressLint;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private final OnWorkoutClickListener listener;
    private final OnFavoriteClickListener favoriteListener;
    private final Map<String, String> workoutToFavoriteMap = new HashMap<>();
    private final Map<String, Long> workoutToDurationMap = new HashMap<>();
    private List<Workout> workouts;

    public WorkoutAdapter(OnWorkoutClickListener listener, OnFavoriteClickListener favoriteListener) {
        this.workouts = new ArrayList<>();
        this.listener = listener;
        this.favoriteListener = favoriteListener;
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
        notifyDataSetChanged();
    }

    public void setFavoriteMapping(Map<String, String> workoutIdToFavoriteId) {
        workoutToFavoriteMap.clear();
        if (workoutIdToFavoriteId != null) {
            workoutToFavoriteMap.putAll(workoutIdToFavoriteId);
        }
        notifyDataSetChanged();
    }

    public void setWorkoutDurations(Map<String, Long> workoutIdToDuration) {
        workoutToDurationMap.clear();
        if (workoutIdToDuration != null) {
            workoutToDurationMap.putAll(workoutIdToDuration);
        }
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateWorkoutDurations(Map<String, Long> newMappings) {
        if (newMappings != null) {
            workoutToDurationMap.putAll(newMappings);
        }
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateFavoriteMapping(Map<String, String> newMappings) {
        if (newMappings != null) {
            workoutToFavoriteMap.putAll(newMappings);
        }
        notifyDataSetChanged();
    }

    public void removeFavorite(String workoutId) {
        if (workoutId != null) {
            workoutToFavoriteMap.remove(workoutId);
            notifyDataSetChanged();
        }
    }

    public void addFavorite(String workoutId, String favoriteId) {
        if (workoutId != null && favoriteId != null) {
            workoutToFavoriteMap.put(workoutId, favoriteId);
            notifyDataSetChanged();
        }
    }

    public void addWorkouts(List<Workout> newWorkouts) {
        int oldSize = workouts.size();
        workouts.addAll(newWorkouts);
        notifyItemRangeInserted(oldSize, newWorkouts.size());
    }

    public void removeWorkout(int position) {
        if (position >= 0 && position < workouts.size()) {
            Workout workout = workouts.get(position);
            if (workout.getId() != null) {
                workoutToFavoriteMap.remove(workout.getId());
            }
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

        if (holder.btnFavorite != null) {
            holder.btnFavorite.setVisibility(View.VISIBLE);
            if (favoriteListener != null) {
                holder.btnFavorite.setEnabled(true);
                String favoriteId = workoutToFavoriteMap.get(workout.getId());
                holder.btnFavorite.setOnClickListener(v ->
                        favoriteListener.onFavoriteClick(workout, position, favoriteId));
            } else {
                holder.btnFavorite.setEnabled(false);
                holder.btnFavorite.setOnClickListener(null);
            }

            boolean isFav = workout.getId() != null && workoutToFavoriteMap.containsKey(workout.getId());
            holder.btnFavorite.setImageResource(isFav ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
        }

        // Set duration from mapping if available (show/hide handled in ViewHolder)
        if (holder.textDuration != null) {
            Long duration = workout.getId() != null ? workoutToDurationMap.get(workout.getId()) : null;
            holder.setDuration(duration);
        }
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    public interface OnWorkoutClickListener {
        void onWorkoutClick(Workout workout);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Workout workout, int position, String favoriteId);
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageWorkout;
        private final TextView textTitle;
        private final TextView textCreator;
        private final TextView textVisibility;
        private final TextView textDetails;
        private final TextView textDuration;
        private final ImageButton btnFavorite;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            imageWorkout = itemView.findViewById(R.id.image_workout);
            textTitle = itemView.findViewById(R.id.text_view_workout_title);
            textCreator = itemView.findViewById(R.id.text_view_workout_creator);
            textVisibility = itemView.findViewById(R.id.text_view_visibility);
            textDetails = itemView.findViewById(R.id.text_view_workout_details);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
            textDuration = itemView.findViewById(R.id.text_view_workout_duration);
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

            if (textDuration != null) {
                textDuration.setVisibility(View.GONE);
            }
        }

        public void setDuration(Long durationInSeconds) {
            if (textDuration == null) return;

            if (durationInSeconds == null || durationInSeconds <= 0) {
                textDuration.setVisibility(View.GONE);
                return;
            }

            long secs = durationInSeconds;
            long hours = secs / 3600;
            long minutes = (secs % 3600) / 60;
            long seconds = secs % 60;

            String text;
            if (hours > 0) {
                // show hours and optional minutes: "1h 5m"
                if (minutes > 0) {
                    text = hours + "h " + minutes + "m";
                } else {
                    text = hours + "h";
                }
            } else if (minutes > 0) {
                // show minutes and optional seconds: "5m 30s" or "5m"
                if (seconds > 0) {
                    text = minutes + "m " + seconds + "s";
                } else {
                    text = minutes + "m";
                }
            } else {
                // less than a minute -> show seconds: "4s"
                text = seconds + "s";
            }

            textDuration.setText(text);
            textDuration.setVisibility(View.VISIBLE);
        }
    }
}