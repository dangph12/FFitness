package com.example.ffitness.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ffitness.R;
import com.example.ffitness.model.Exercise;
import com.example.ffitness.model.WorkoutSession;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private List<WorkoutSession> sessions = new ArrayList<>();

    public void setExercises(List<WorkoutSession> newSessions) {
        this.sessions = newSessions != null ? newSessions : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        WorkoutSession session = sessions.get(position);
        Exercise exercise = session.getExercise();

        if (exercise != null) {
            holder.textTitle.setText(exercise.getTitle());
            holder.textDifficulty.setText(
                    "Difficulty: " + exercise.getDifficulty() + " | Type: " + exercise.getType()
            );

            int[] repsArray = session.getSets();
            if (repsArray != null && repsArray.length > 0) {
                StringBuilder repsBuilder = new StringBuilder();
                for (int i = 0; i < repsArray.length; i++) {
                    repsBuilder.append(repsArray[i]);
                    if (i < repsArray.length - 1) repsBuilder.append(", ");
                }
                holder.textSets.setText("Sets: " + repsArray.length + " • Reps: " + repsBuilder);
            } else {
                holder.textSets.setText("No sets info");
            }

            if (exercise.getTutorial() != null && !exercise.getTutorial().isEmpty()) {
                new LoadImageTask(holder.imageExercise).execute(exercise.getTutorial());
            } else {
                holder.imageExercise.setImageResource(R.drawable.logo);
            }
        }
    }

    @Override
    public int getItemCount() {
        return sessions != null ? sessions.size() : 0;
    }

    private static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private final ImageView imageView;

        public LoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setDoInput(true);
                connection.connect();
                try (InputStream input = connection.getInputStream()) {
                    return BitmapFactory.decodeStream(input);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.drawable.logo);
            }
        }
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        ImageView imageExercise;
        TextView textTitle, textDifficulty, textSets;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            imageExercise = itemView.findViewById(R.id.image_exercise);
            textTitle = itemView.findViewById(R.id.text_title);
            textDifficulty = itemView.findViewById(R.id.text_difficulty);
            textSets = itemView.findViewById(R.id.text_sets);
        }
    }
}
