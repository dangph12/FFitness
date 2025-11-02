package com.example.ffitness.ui;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ffitness.MainActivity;
import com.example.ffitness.R;
import com.example.ffitness.repository.UserRepository;
import com.example.ffitness.util.SharedPreferencesManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class OnboardingActivity extends AppCompatActivity {
    private LinearLayout step1Layout, step2Layout, step3Layout, step4Layout;

    private TextInputEditText inputDateOfBirth;
    private RadioGroup genderGroup;
    private MaterialButton nextToStep2;

    private TextInputEditText inputHeight, inputWeight;
    private TextView bmiResultText;
    private MaterialButton backToStep1, nextToStep3;

    private TextInputEditText inputTargetWeight;
    private RadioGroup fitnessGoalGroup;
    private AutoCompleteTextView dietDropdown;
    private MaterialButton backToStep2, nextToStep4;

    private TextView summaryText;
    private MaterialButton backToStep3, submitButton;

    private SharedPreferencesManager prefsManager;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        prefsManager = new SharedPreferencesManager(this);
        userRepository = new UserRepository(getApplication());

        initViews();
        setupClickListeners();
        setupBmiCalculator();
        setupDietDropdown();

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        step1Layout = findViewById(R.id.step1Layout);
        step2Layout = findViewById(R.id.step2Layout);
        step3Layout = findViewById(R.id.step3Layout);
        step4Layout = findViewById(R.id.step4Layout);

        inputDateOfBirth = findViewById(R.id.inputDateOfBirth);
        genderGroup = findViewById(R.id.genderGroup);
        nextToStep2 = findViewById(R.id.nextToStep2);

        inputHeight = findViewById(R.id.inputHeight);
        inputWeight = findViewById(R.id.inputWeight);
        bmiResultText = findViewById(R.id.bmiResultText);
        backToStep1 = findViewById(R.id.backToStep1);
        nextToStep3 = findViewById(R.id.nextToStep3);

        inputTargetWeight = findViewById(R.id.inputTargetWeight);
        fitnessGoalGroup = findViewById(R.id.fitnessGoalGroup);
        dietDropdown = findViewById(R.id.dietDropdown);
        backToStep2 = findViewById(R.id.backToStep2);
        nextToStep4 = findViewById(R.id.nextToStep4);

        summaryText = findViewById(R.id.summaryText);
        backToStep3 = findViewById(R.id.backToStep3);
        submitButton = findViewById(R.id.submitButton);
    }

    private void setupClickListeners() {
        inputDateOfBirth.setOnClickListener(v -> showDatePickerDialog());

        nextToStep2.setOnClickListener(v -> {
            if (validateStep1()) {
                step1Layout.setVisibility(View.GONE);
                step2Layout.setVisibility(View.VISIBLE);
            }
        });

        backToStep1.setOnClickListener(v -> {
            step2Layout.setVisibility(View.GONE);
            step1Layout.setVisibility(View.VISIBLE);
        });

        nextToStep3.setOnClickListener(v -> {
            if (validateStep2()) {
                step2Layout.setVisibility(View.GONE);
                step3Layout.setVisibility(View.VISIBLE);
            }
        });

        backToStep2.setOnClickListener(v -> {
            step3Layout.setVisibility(View.GONE);
            step2Layout.setVisibility(View.VISIBLE);
        });

        nextToStep4.setOnClickListener(v -> {
            if (validateStep3()) {
                updateSummaryText();
                step3Layout.setVisibility(View.GONE);
                step4Layout.setVisibility(View.VISIBLE);
            }
        });

        backToStep3.setOnClickListener(v -> {
            step4Layout.setVisibility(View.GONE);
            step3Layout.setVisibility(View.VISIBLE);
        });

        submitButton.setOnClickListener(v -> submitOnboardingData());
    }

    private boolean validateStep1() {
        String dob = inputDateOfBirth.getText().toString().trim();
        int selectedGenderId = genderGroup.getCheckedRadioButtonId();

        if (dob.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày sinh", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedGenderId == -1) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validateStep2() {
        String height = inputHeight.getText().toString().trim();
        String weight = inputWeight.getText().toString().trim();

        if (height.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập chiều cao", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (weight.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập cân nặng", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            float h = Float.parseFloat(height);
            float w = Float.parseFloat(weight);

            if (h <= 0 || w <= 0) {
                Toast.makeText(this, "Chiều cao và cân nặng phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validateStep3() {
        String targetWeight = inputTargetWeight.getText().toString().trim();
        int selectedFitnessGoalId = fitnessGoalGroup.getCheckedRadioButtonId();
        String diet = dietDropdown.getText().toString().trim();


        if (targetWeight.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập cân nặng mục tiêu", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedFitnessGoalId == -1) {
            Toast.makeText(this, "Vui lòng chọn mục tiêu của bạn", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (diet.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn chế độ ăn", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            float tw = Float.parseFloat(targetWeight);
            if (tw <= 0) {
                Toast.makeText(this, "Cân nặng mục tiêu phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số hợp lệ cho cân nặng mục tiêu", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void submitOnboardingData() {
        submitButton.setEnabled(false);

        String userId = prefsManager.getUserId();
        String token = prefsManager.getAccessToken();

        Log.d(TAG, "=== ONBOARDING SUBMISSION DEBUG ===");
        Log.d(TAG, "UserId: " + userId);
        Log.d(TAG, "Token exists: " + (token != null && !token.isEmpty()));
        if (token != null) {
            Log.d(TAG, "Token: " + token);
        } else {
            Log.e(TAG, "TOKEN IS NULL! User needs to login again.");
        }

        if (token == null || token.isEmpty()) {
            runOnUiThread(() -> {
                submitButton.setEnabled(true);
                Toast.makeText(OnboardingActivity.this,
                        "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!",
                        Toast.LENGTH_LONG).show();

                Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
            return;
        }

        String gender = getSelectedGender();
        String dob = inputDateOfBirth.getText().toString().trim();
        double height = Double.parseDouble(inputHeight.getText().toString().trim());
        double weight = Double.parseDouble(inputWeight.getText().toString().trim());
        double bmi = calculateBmi(height, weight);
        double targetWeight = Double.parseDouble(inputTargetWeight.getText().toString().trim());
        String diet = dietDropdown.getText().toString().trim();
        String fitnessGoal = getSelectedFitnessGoal();

        Log.d(TAG, "Request Data:");
        Log.d(TAG, "- Gender: " + gender);
        Log.d(TAG, "- DOB: " + dob);
        Log.d(TAG, "- Height: " + height);
        Log.d(TAG, "- Weight: " + weight);
        Log.d(TAG, "- BMI: " + bmi);
        Log.d(TAG, "- Target Weight: " + targetWeight);
        Log.d(TAG, "- Diet: " + diet);
        Log.d(TAG, "- Fitness Goal: " + fitnessGoal);
        Log.d(TAG, "===================================");

        userRepository.completeOnboarding(userId, gender, dob, height, weight, bmi,
                targetWeight, diet, fitnessGoal, new UserRepository.UserOnboardingCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Onboarding completed successfully!");
                        runOnUiThread(() -> {
                            Toast.makeText(OnboardingActivity.this,
                                    "Hoàn thành thiết lập thông tin!", Toast.LENGTH_SHORT).show();

                            prefsManager.setOnboardingCompleted(true);

                            Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "Onboarding error: " + errorMessage);
                        runOnUiThread(() -> {
                            submitButton.setEnabled(true);

                            if (errorMessage.contains("401") || errorMessage.toLowerCase().contains("unauthorized")) {
                                Toast.makeText(OnboardingActivity.this,
                                        "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!",
                                        Toast.LENGTH_LONG).show();

                                prefsManager.clear();
                                Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(OnboardingActivity.this,
                                        "Lỗi: " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
    }

    private String getSelectedGender() {
        int selectedId = genderGroup.getCheckedRadioButtonId();
        if (selectedId == -1) return "";

        RadioButton selectedRadioButton = findViewById(selectedId);
        String vietnameseGender = selectedRadioButton.getText().toString();

        switch (vietnameseGender) {
            case "Nam":
                return "male";
            case "Nữ":
                return "female";
            default:
                return "other";
        }
    }

    private String getSelectedFitnessGoal() {
        int selectedId = fitnessGoalGroup.getCheckedRadioButtonId();
        if (selectedId == -1) return "";

        RadioButton selectedRadioButton = findViewById(selectedId);
        String vietnameseGoal = selectedRadioButton.getText().toString();

        switch (vietnameseGoal) {
            case "Giảm cân":
                return "Lose Weight";
            case "Tăng cơ":
                return "Build Muscle";
            case "Sống khỏe":
                return "To be Healthy";
            default:
                return "";
        }
    }

    private double calculateBmi(double height, double weight) {
        double heightInM = height / 100;
        return weight / (heightInM * heightInM);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                OnboardingActivity.this,
                (view, yearSelected, monthOfYear, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(),
                            "%02d/%02d/%d", dayOfMonth, monthOfYear + 1, yearSelected);
                    inputDateOfBirth.setText(selectedDate);
                },
                year, month, day);

        calendar.add(Calendar.YEAR, -10);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void setupBmiCalculator() {
        TextWatcher bmiTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateAndDisplayBmi();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        inputHeight.addTextChangedListener(bmiTextWatcher);
        inputWeight.addTextChangedListener(bmiTextWatcher);
    }

    private void calculateAndDisplayBmi() {
        String heightStr = inputHeight.getText().toString();
        String weightStr = inputWeight.getText().toString();
        if (!heightStr.isEmpty() && !weightStr.isEmpty()) {
            try {
                float heightInCm = Float.parseFloat(heightStr);
                float weightInKg = Float.parseFloat(weightStr);
                float heightInM = heightInCm / 100;
                if (heightInM > 0 && weightInKg > 0) {
                    float bmi = weightInKg / (heightInM * heightInM);
                    bmiResultText.setText(String.format("Chỉ số BMI của bạn là: %.1f", bmi));
                } else {
                    bmiResultText.setText("Chiều cao và cân nặng phải lớn hơn 0");
                }
            } catch (NumberFormatException e) {
                bmiResultText.setText("Vui lòng nhập số hợp lệ");
            }
        } else {
            bmiResultText.setText("BMI của bạn sẽ hiển thị ở đây");
        }
    }

    // THÊM MỚI: Cài đặt các tùy chọn cho AutoCompleteTextView
    private void setupDietDropdown() {
        String[] diets = getResources().getStringArray(R.array.diet_options); // Giả sử bạn có một string-array trong strings.xml
        // Hoặc hardcode: String[] diets = new String[]{"Truyền thống", "Keto", "Low Carb", "Eat Clean"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                diets
        );
        dietDropdown.setAdapter(adapter);
    }

    private void updateSummaryText() {
        String dateOfBirth = inputDateOfBirth.getText().toString();
        String gender = "Chưa chọn";
        int selectedGenderId = genderGroup.getCheckedRadioButtonId();
        if (selectedGenderId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedGenderId);
            gender = selectedRadioButton.getText().toString();
        }

        String height = inputHeight.getText().toString();
        String weight = inputWeight.getText().toString();
        String bmiResult = bmiResultText.getText().toString();
        String bmiValue = "Chưa tính";
        if (bmiResult.contains(":")) {
            bmiValue = bmiResult.substring(bmiResult.indexOf(":") + 1).trim();
        }

        String targetWeight = inputTargetWeight.getText().toString();

        // THAY ĐỔI: Lấy mục tiêu và chế độ ăn từ các view mới
        String fitnessGoal = getSelectedFitnessGoal();
        if (fitnessGoal.isEmpty()) {
            fitnessGoal = "Chưa chọn";
        }

        String diet = dietDropdown.getText().toString();
        if (diet.isEmpty()) {
            diet = "Chưa chọn";
        }

        String summary = "Giới tính: " + gender + "\n" +
                "Ngày sinh: " + (dateOfBirth.isEmpty() ? "Chưa chọn" : dateOfBirth) + "\n\n" +
                "Chiều cao: " + (height.isEmpty() ? "Chưa nhập" : height + " cm") + "\n" +
                "Cân nặng: " + (weight.isEmpty() ? "Chưa nhập" : weight + " kg") + "\n" +
                "Chỉ số BMI: " + bmiValue + "\n\n" +
                "Cân nặng mục tiêu: " + (targetWeight.isEmpty() ? "Chưa nhập" : targetWeight + " kg") + "\n" +
                "Mục tiêu của bạn: " + fitnessGoal + "\n" +
                "Chế độ ăn: " + diet;

        summaryText.setText(summary);
    }

    // CÁC PHƯƠNG THỨC BỊ XÓA:
    // private String getSelectedDiet() { ... }
    // private String determineFitnessGoal(double currentWeight, double targetWeight) { ... }
}