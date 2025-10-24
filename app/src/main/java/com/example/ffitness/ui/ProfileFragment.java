package com.example.ffitness.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ffitness.MainActivity;
import com.example.ffitness.R;
import com.example.ffitness.util.SharedPreferencesManager;

public class ProfileFragment extends Fragment {

    private Button btnLogout;
    private SharedPreferencesManager prefsManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        prefsManager = new SharedPreferencesManager(requireContext());
        btnLogout = view.findViewById(R.id.btn_logout);
        
        btnLogout.setOnClickListener(v -> {
            // Clear session
            prefsManager.clear();
            
            // Show toast
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            
            // Close current activity
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }
}
