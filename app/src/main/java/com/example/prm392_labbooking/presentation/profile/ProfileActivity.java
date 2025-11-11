package com.example.prm392_labbooking.presentation.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.prm392_labbooking.R;
import com.example.prm392_labbooking.data.firebase.FirebaseAuthService;
import com.example.prm392_labbooking.presentation.base.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileActivity extends BaseActivity {
    private TextView tvEmail, tvDisplayName;
    private TextView btnEditName;
    private FirebaseAuthService authService;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initLoadingOverlay();

        authService = new FirebaseAuthService();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        tvEmail = findViewById(R.id.tv_email);
        tvDisplayName = findViewById(R.id.tv_display_name);
        btnEditName = findViewById(R.id.btn_edit_name);

        loadUserInfo();

        btnEditName.setOnClickListener(v -> showEditNameDialog());
    }

    private void loadUserInfo() {
        if (currentUser != null) {
            String email = currentUser.getEmail();
            String displayName = currentUser.getDisplayName();

            tvEmail.setText(email != null ? email : getString(R.string.not_available));
            tvDisplayName.setText(displayName != null && !displayName.isEmpty() 
                ? displayName 
                : getString(R.string.not_set));
        } else {
            tvEmail.setText(getString(R.string.not_available));
            tvDisplayName.setText(getString(R.string.not_available));
        }
    }

    private void showEditNameDialog() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        input.setHint(getString(R.string.display_name));
        if (currentUser != null && currentUser.getDisplayName() != null) {
            input.setText(currentUser.getDisplayName());
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.edit_display_name)
                .setMessage(R.string.enter_new_display_name)
                .setView(input)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        updateDisplayName(newName);
                    } else {
                        Toast.makeText(this, R.string.display_name_required, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void updateDisplayName(String newName) {
        if (currentUser == null) return;

        showLoading();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();

        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    hideLoading();
                    if (task.isSuccessful()) {
                        Toast.makeText(this, R.string.display_name_updated, Toast.LENGTH_SHORT).show();
                        loadUserInfo();
                    } else {
                        Toast.makeText(this, R.string.error_updating_name, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

