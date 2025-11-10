package com.example.ffitness.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ffitness.MainActivity;
import com.example.ffitness.R;
import com.example.ffitness.repository.AuthRepository;
import com.example.ffitness.util.JwtDecoder;
import com.example.ffitness.util.SharedPreferencesManager;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private SharedPreferencesManager prefsManager;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefsManager = new SharedPreferencesManager(this);
        authRepository = new AuthRepository(getApplication());

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // ======= VALIDATION =======
        if (email.isEmpty()) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return;
        }
        // ===========================

        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        authRepository.login(email, password, new AuthRepository.LoginCallback() {
            @Override
            public void onSuccess(String accessToken) {
                Log.d(TAG, "Login successful, got access token");

                String userId = JwtDecoder.getUserIdFromToken(accessToken);
                Boolean profileCompleted = JwtDecoder.getOnboardingCompleteFromToken(accessToken);

                if (userId != null) {
                    prefsManager.saveAccessToken(accessToken);
                    prefsManager.saveUserId(userId);

                    runOnUiThread(() -> {
                        if (Boolean.TRUE.equals(profileCompleted)) {
                            Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            navigateToMain();
                        } else {
                            Toast.makeText(LoginActivity.this, "Please complete your profile!", Toast.LENGTH_SHORT).show();
                            navigateToOnboarding();
                        }
                    });
                } else {
                    Log.e(TAG, "Failed to decode user ID from token");
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Login failed: Invalid token", Toast.LENGTH_SHORT).show();
                        resetLogin();
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> handleLoginError(errorMessage));
            }
        });
    }

    private void resetLogin() {
        btnLogin.setEnabled(true);
        btnLogin.setText("Login");
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToOnboarding() {
        Intent intent = new Intent(this, OnboardingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
