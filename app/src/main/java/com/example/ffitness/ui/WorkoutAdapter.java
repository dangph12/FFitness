package com.example.ffitness.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ffitness.R;
import com.example.ffitness.model.Workout;
import com.example.ffitness.model.WorkoutSession;

import java.util.ArrayList;
import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private List<Workout> workouts;
    private OnWorkoutClickListener listener;

    public interface OnWorkoutClickListener {
        void onWorkoutClick(Workout workout);
    }

    public WorkoutAdapter(OnWorkoutClickListener listener) {
        this.workouts = new ArrayList<>();
        this.listener = listener;
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
        notifyDataSetChanged();
    }

    public void addWorkouts(List<Workout> newWorkouts) {
        int oldSize = workouts.size();
        workouts.addAll(newWorkouts);
        notifyItemRangeInserted(oldSize, newWorkouts.size());
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
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageWorkout;
        private TextView textTitle, textCreator, textVisibility, textDetails;
        private ImageButton buttonStart;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            imageWorkout = itemView.findViewById(R.id.image_workout);
            textTitle = itemView.findViewById(R.id.text_view_workout_title);
            textCreator = itemView.findViewById(R.id.text_view_workout_creator);
            textVisibility = itemView.findViewById(R.id.text_view_visibility);
            textDetails = itemView.findViewById(R.id.text_view_workout_details);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onWorkoutClick(workouts.get(position));
                }
            });
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

            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                imageWorkout.setImageResource(R.drawable.logo);
                return;
            }
        }
    }
}
