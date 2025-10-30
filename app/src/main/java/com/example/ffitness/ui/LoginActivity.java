package com.example.ffitness.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

        if (prefsManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        authRepository.login(email, password, new AuthRepository.LoginCallback() {
            @Override
            public void onSuccess(String accessToken) {
                Log.d(TAG, "Login successful, got access token");

                String userId = JwtDecoder.getUserIdFromToken(accessToken);

                if (userId != null) {
                    Log.d(TAG, "Decoded user ID: " + userId);

                    prefsManager.saveAccessToken(accessToken);
                    prefsManager.saveUserId(userId);

                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        navigateToMain();
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
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    resetLogin();
                });
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
}