package com.example.ffitness.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.ffitness.MainActivity;
import com.example.ffitness.R;
import com.example.ffitness.model.User;
import com.example.ffitness.repository.UserRepository;
import com.example.ffitness.util.SharedPreferencesManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private SharedPreferencesManager prefsManager;
    private UserRepository userRepository;

    // UI Components
    private ImageView ivAvatar;
    private TextView tvUserName;
    private TextView tvUserEmail;
    private TextView tvActiveStatus;
    private TextView tvProfileStatus;
    private TextView tvGender;
    private TextView tvAge;
    private TextView tvAccountEmail;
    private TextView tvDateOfBirth;
    private ProgressBar progressBar;
    private LinearLayout contentLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupClickListeners(view);
        loadUserProfile();
    }

    private void initializeViews(View view) {
        prefsManager = new SharedPreferencesManager(requireContext());
        userRepository = new UserRepository(requireActivity().getApplication());

        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvActiveStatus = view.findViewById(R.id.tvActiveStatus);
        tvProfileStatus = view.findViewById(R.id.tvProfileStatus);
        tvGender = view.findViewById(R.id.tvGender);
        tvAge = view.findViewById(R.id.tvAge);
        tvAccountEmail = view.findViewById(R.id.tvAccountEmail);
        tvDateOfBirth = view.findViewById(R.id.tvDateOfBirth);
        progressBar = view.findViewById(R.id.progressBar);
        contentLayout = view.findViewById(R.id.contentLayout);
    }

    private void setupClickListeners(View view) {
        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.getSupportFragmentManager().popBackStack(null,
                        androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
                mainActivity.navigateToFragment(new HomeFragment(), false);
            }
        });

        Button btnFavorites = view.findViewById(R.id.btn_favorites);
        btnFavorites.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.navigateToFragment(new FavoriteFragment(), true);
            }
        });

        Button btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> handleLogout());
    }

    private void loadUserProfile() {
        String userId = prefsManager.getUserId();

        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            handleLogout();
            return;
        }

        showLoading(true);

        userRepository.getUserById(userId, new UserRepository.UserProfileCallback() {
            @Override
            public void onSuccess(User user) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        displayUserProfile(user);
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(requireContext(),
                                "Failed to load profile: " + errorMessage,
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void displayUserProfile(User user) {
        if (user == null) return;

        if (user.getName() != null && !user.getName().isEmpty()) {
            tvUserName.setText(user.getName());
        } else {
            tvUserName.setText("User");
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            tvUserEmail.setText(user.getEmail());
            tvAccountEmail.setText(user.getEmail());
        }

        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            Glide.with(this)
                    .load(user.getAvatar())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .circleCrop()
                    .into(ivAvatar);
        }

        if (user.getIsActive() != null && user.getIsActive()) {
            tvActiveStatus.setText("● Active");
            tvActiveStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvActiveStatus.setText("● Inactive");
            tvActiveStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        if (user.getProfileCompleted() != null && user.getProfileCompleted()) {
            tvProfileStatus.setText("✓ Profile Completed");
            tvProfileStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvProfileStatus.setText("○ Profile Incomplete");
            tvProfileStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        }

        if (user.getGender() != null && !user.getGender().isEmpty()) {
            tvGender.setText("Gender: " + capitalizeFirstLetter(user.getGender()));
        } else {
            tvGender.setText("Gender: Null");
        }

        // Calculate and display age from DOB and also display the DOB
        if (user.getDob() != null) {
            int age = calculateAge(user.getDob());
            tvAge.setText("Age: " + age);

            // Format and display Date of Birth
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            tvDateOfBirth.setText(sdf.format(user.getDob()));
        } else {
            tvAge.setText("Age: Not set");
            tvDateOfBirth.setText("Not set");
        }
    }

    private int calculateAge(Date birthDate) {
        if (birthDate == null) return 0;

        Calendar birth = Calendar.getInstance();
        birth.setTime(birthDate);

        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }

    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        if (contentLayout != null) {
            contentLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        }
    }

    private void handleLogout() {
        prefsManager.clear();

        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private long calculateDaysSince(Date createdDate) {
        if (createdDate == null) return 0;

        Date today = new Date();

        long diffInMillis = today.getTime() - createdDate.getTime();
        long days = diffInMillis / (1000 * 60 * 60 * 24);

        return days;
    }

    private String getInitialName(String name) {
        if (name == null || name.trim().isEmpty()) return "?";
        return name.trim().substring(0, 1).toUpperCase();
    }

    private double calculateBMI(Double weightKg, Double heightCm) {
        if (weightKg == null || heightCm == null || heightCm == 0) return 0;
        double heightM = heightCm / 100;
        return weightKg / (heightM * heightM);
    }

}