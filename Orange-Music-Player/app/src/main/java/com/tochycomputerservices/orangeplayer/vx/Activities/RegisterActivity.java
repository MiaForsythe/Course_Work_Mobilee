package com.tochycomputerservices.orangeplayer.vx.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tochycomputerservices.orangeplayer.vx.Auth.UserLoginHelper;
import com.tochycomputerservices.orangeplayer.vx.Common;
import com.tochycomputerservices.orangeplayer.vx.R;

public class RegisterActivity extends AppCompatActivity {

    private UserLoginHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new UserLoginHelper(Common.getInstance());
        setContentView(R.layout.activity_registration);

        Button buttonReg = findViewById(R.id.buttonRegister);
        EditText login = findViewById(R.id.editTextUsername);
        EditText pass = findViewById(R.id.editTextPassword);
        EditText em = findViewById(R.id.editTextEmail);
        EditText incorrect = findViewById(R.id.editTextIncorrect);
        TextView textViewBackToLogin = findViewById(R.id.textViewBackToLogin); // Текстовая ссылка

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = login.getText().toString().trim();
                String password = pass.getText().toString().trim();
                String email = em.getText().toString().trim();

                if (!validateInput(username, password, email)) {
                    return;
                }

                if (db.checkUserExists(username)) {
                    incorrect.setVisibility(View.VISIBLE);
                } else {
                    db.registerUser(username, password);
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    RegisterActivity.this.finish();
                }
            }
        });

        // Обработчик нажатия текстовой ссылки "Back to Login"
        textViewBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Возвращаемся на активность логина
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Закрываем текущую активность регистрации
            }
        });
    }

    private boolean validateInput(String username, String password, String email) {
        if (username.isEmpty()) {
            showToast("Username is required");
            return false;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Enter a valid email address");
            return false;
        }

        if (password.isEmpty() || password.length() < 6) {
            showToast("Password must be at least 6 characters");
            return false;
        }

        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
