package com.tudio.scregproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.Nullable;

public class ForgotPasswordFragment extends Fragment {
    private EditText etResetEmail;
    private Button btnResetPassword;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        mAuth = FirebaseAuth.getInstance();
        etResetEmail = view.findViewById(R.id.editTextResetEmail);
        btnResetPassword = view.findViewById(R.id.buttonResetPassword);

        btnResetPassword.setOnClickListener(v -> {
            String email = etResetEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(getContext(), "Email giriniz", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(getContext(), "Şifre sıfırlama maili gönderildi", Toast.LENGTH_LONG).show();
                        requireActivity().getSupportFragmentManager().popBackStack(); // Fragment'i kapat
                        requireActivity().findViewById(R.id.loginFormLayout).setVisibility(View.VISIBLE); // Formu geri getir
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Hata: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );

        });

        return view;
    }
}
