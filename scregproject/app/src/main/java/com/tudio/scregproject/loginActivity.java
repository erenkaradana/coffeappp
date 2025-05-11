package com.tudio.scregproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;

public class loginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword;
    private FirebaseAuth mAuth;
    private FragmentManager fragmentManager;
    private TextView  tvGoToRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        fragmentManager = getSupportFragmentManager();

        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.buttonLogin);
        tvForgotPassword = findViewById(R.id.textViewForgotPassword);
        tvGoToRegister = findViewById(R.id.textViewGoToRegister);

        btnLogin.setOnClickListener(v -> loginUser());

        tvForgotPassword.setOnClickListener(v -> {
            findViewById(R.id.loginFormLayout).setVisibility(View.GONE); // Login UI'yı gizle
            findViewById(R.id.hosgeldinText).setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.loginContainer, new ForgotPasswordFragment())
                    .addToBackStack(null)
                    .commit();
        });
        fragmentManager.addOnBackStackChangedListener(() -> {
            // Eğer fragment stack boşsa (yani fragment kapatılmışsa), login formu geri getir
            if (fragmentManager.getBackStackEntryCount() == 0) {
                findViewById(R.id.loginFormLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.hosgeldinText).setVisibility(View.VISIBLE);

            }
        });


        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(loginActivity.this, registerActivity.class));
            finish();
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email ve şifre boş olamaz", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Giriş başarılı!", Toast.LENGTH_SHORT).show();
                    // MainActivity'e geç
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
