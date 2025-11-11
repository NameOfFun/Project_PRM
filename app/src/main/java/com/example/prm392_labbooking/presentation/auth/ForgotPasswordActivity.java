package com.example.prm392_labbooking.presentation.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.example.prm392_labbooking.R;
import com.example.prm392_labbooking.data.firebase.FirebaseAuthService;
import com.example.prm392_labbooking.data.repository.AuthRepositoryImpl;
import com.example.prm392_labbooking.domain.repository.AuthRepository;
import com.example.prm392_labbooking.navigation.NavigationManager;
import com.example.prm392_labbooking.presentation.base.BaseActivity;
import com.example.prm392_labbooking.utils.ValidationUtils;

public class ForgotPasswordActivity extends BaseActivity {
    private EditText emailEditText;
    private Button resetPasswordButton;
    private AuthRepository authRepository;
    private FrameLayout loadingOverlay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        findViewById(R.id.backToLoginButton).setOnClickListener(v -> NavigationManager.goToLogin(this));
        emailEditText = findViewById(R.id.emailEditText);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        authRepository = new AuthRepositoryImpl(new FirebaseAuthService());
        loadingOverlay = findViewById(R.id.loadingOverlay);
        resetPasswordButton.setOnClickListener(v -> onResetPassword());
    }

    private void onResetPassword() {
        String email = emailEditText.getText().toString().trim();
        if (!ValidationUtils.isValidEmail(email)) {
            emailEditText.setError(getString(R.string.invalid_email));
            emailEditText.requestFocus();
            return;
        }
        showLoading();
        resetPasswordButton.setEnabled(false);
        authRepository.resetPassword(email)
            .addOnCompleteListener(task -> {
                hideLoading();
                resetPasswordButton.setEnabled(true);
                if (task.isSuccessful()) {
                    Toast.makeText(this, getString(R.string.reset_password_email_sent), Toast.LENGTH_LONG).show();
                    // Delay a bit before navigating to login
                    emailEditText.postDelayed(() -> {
                        NavigationManager.goToLogin(this);
                        finish();
                    }, 1500);
                } else {
                    String errorMessage = getString(R.string.error);
                    if (task.getException() != null) {
                        Exception exception = task.getException();
                        String exceptionMessage = exception.getMessage();
                        if (exceptionMessage != null) {
                            if (exceptionMessage.contains("user-not-found")) {
                                errorMessage = "Email này chưa được đăng ký trong hệ thống.";
                            } else if (exceptionMessage.contains("invalid-email")) {
                                errorMessage = "Email không hợp lệ. Vui lòng kiểm tra lại.";
                            } else if (exceptionMessage.contains("network")) {
                                errorMessage = "Lỗi kết nối mạng. Vui lòng kiểm tra kết nối internet và thử lại.";
                            } else {
                                errorMessage = "Không thể gửi email đặt lại mật khẩu: " + exceptionMessage;
                            }
                        }
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }
            });
    }

    @Override
    public void showLoading() {
        if (loadingOverlay != null) loadingOverlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        if (loadingOverlay != null) loadingOverlay.setVisibility(View.GONE);
    }
}
