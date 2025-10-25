package com.example.ffitness.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
        
        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            com.example.ffitness.MainActivity mainActivity = (com.example.ffitness.MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
                mainActivity.navigateToFragment(new HomeFragment(), false);
            }
        });
        
        prefsManager = new SharedPreferencesManager(requireContext());
        btnLogout = view.findViewById(R.id.btn_logout);
        
        btnLogout.setOnClickListener(v -> {
            prefsManager.clear();
            
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }
}
