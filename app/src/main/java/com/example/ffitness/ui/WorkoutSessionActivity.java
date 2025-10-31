package com.example.ffitness.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.ffitness.R;
import com.example.ffitness.model.History;
import com.example.ffitness.model.Workout;
import com.example.ffitness.model.WorkoutSession;
import com.example.ffitness.repository.HistoryRepository;
import com.example.ffitness.util.SharedPreferencesManager;

import java.util.List;
import java.util.Locale;

public class WorkoutSessionActivity extends AppCompatActivity {

    private static final String TAG = "WorkoutSessionActivity";

    private TextView textTimer;
    private Button btnAction;

    private List<WorkoutSession> workoutSessions;
    private int currentExerciseIndex = 0;
    private ExerciseSessionFragment currentFragment;

    private Handler timerHandler;
    private Runnable timerRunnable;
    private long startTime;
    private long elapsedTime = 0;

    private HistoryRepository historyRepository;
    private SharedPreferencesManager prefsManager;
    private String workoutId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_workout_session);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        textTimer = findViewById(R.id.text_timer);
        Button btnFinishWorkout = findViewById(R.id.btn_finish_workout);
        btnAction = findViewById(R.id.btn_action);

        historyRepository = new HistoryRepository(getApplication());
        prefsManager = new SharedPreferencesManager(this);

        Workout workout = (Workout) getIntent().getSerializableExtra("workout");

        if (workout != null && workout.getExercises() != null && !workout.getExercises().isEmpty()) {
            workoutId = workout.getId();
            workoutSessions = workout.getExercises();
            loadExercise(0);
        } else {
            Toast.makeText(this, "No exercises found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        startTimer();
        btnFinishWorkout.setOnClickListener(v -> finishWorkout());
        btnAction.setOnClickListener(v -> handleActionButton());
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
        timerHandler = new Handler(Looper.getMainLooper());
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                elapsedTime = (System.currentTimeMillis() - startTime) / 1000; // seconds
                updateTimerDisplay();
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.post(timerRunnable);
    }

    private void updateTimerDisplay() {
        long minutes = elapsedTime / 60;
        long seconds = elapsedTime % 60;
        textTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    private void stopTimer() {
        if (timerHandler != null && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    private void loadExercise(int index) {
        currentExerciseIndex = index;
        WorkoutSession session = workoutSessions.get(index);

        currentFragment = ExerciseSessionFragment.newInstance(
                session,
                index,
                workoutSessions.size()
        );

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, currentFragment);
        transaction.commit();

        getSupportFragmentManager().executePendingTransactions();
        updateActionButtonText();
    }

    private void handleActionButton() {
        if (currentFragment == null) return;

        currentFragment.markSetCompleted();

        if (currentFragment.isLastSet()) {
            if (currentExerciseIndex < workoutSessions.size() - 1) {
                loadExercise(currentExerciseIndex + 1);
            } else {
                finishWorkout();
            }
        } else {
            updateActionButtonText();
        }
    }

    private void updateActionButtonText() {
        if (currentFragment == null) return;

        boolean isLastSet = currentFragment.isLastSet();
        boolean isLastExercise = currentExerciseIndex >= workoutSessions.size() - 1;

        if (isLastSet) {
            if (isLastExercise) {
                btnAction.setText("Finish Workout");
            } else {
                btnAction.setText("Next Exercise");
            }
        } else {
            btnAction.setText("Complete Set");
        }
    }

    private void finishWorkout() {
        stopTimer();

        String userId = prefsManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Finishing workout. Time: " + elapsedTime + "s, User: " + userId + ", Workout: " + workoutId);

        historyRepository.saveHistory(userId, workoutId, elapsedTime, new HistoryRepository.HistoryAddCallback() {
            @Override
            public void onSuccess(History history) {
                runOnUiThread(() -> {
                    Toast.makeText(WorkoutSessionActivity.this,
                            "Workout completed! Time: " + formatTime(elapsedTime),
                            Toast.LENGTH_LONG).show();
                    finish();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(WorkoutSessionActivity.this,
                            "Failed to save workout: " + errorMessage,
                            Toast.LENGTH_LONG).show();
                    finish();
                });
            }
        });
    }

    private String formatTime(long seconds) {
        long minutes = seconds / 60;
        long secs = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, secs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}