package com.tochycomputerservices.orangeplayer.vx.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.tochycomputerservices.orangeplayer.vx.Auth.UserLoginHelper;
import com.tochycomputerservices.orangeplayer.vx.Common;
import com.tochycomputerservices.orangeplayer.vx.LauncherActivity.MainActivity;
import com.tochycomputerservices.orangeplayer.vx.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private UserLoginHelper db;
    private boolean registrationAllowed = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new UserLoginHelper(Common.getInstance());
        setContentView(R.layout.activity_login);

        Button buttonLogin = findViewById(R.id.buttonLogin);
        EditText login = findViewById(R.id.editTextUsername);
        EditText pass = findViewById(R.id.editTextPassword);
        EditText incorrect = findViewById(R.id.editTextIncorrect);
        TextView reg = findViewById(R.id.textViewRegister);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registrationAllowed) {
                    String username = login.getText().toString();
                    String password = pass.getText().toString();

                    // Проверяем валидность введенных данных
                    if (validateInput(username, password)) {
                        // Если введенные данные валидны, проверяем пользователя и переходим на главный экран
                        if (db.loginUser(username, password)) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            incorrect.setVisibility(View.VISIBLE);
                        }
                    } else {
                        // Если данные невалидны, показываем окно запроса лицензионного ключа
                        showLicenseKeyDialog();
                    }
                } else {
                    // Проверяем данные для входа при отсутствии возможности регистрации
                    if (db.loginUser(login.getText().toString(), pass.getText().toString())) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        incorrect.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переходим на экран регистрации при нажатии на "Register"
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Метод для отображения окна запроса лицензионного ключа
    private void showLicenseKeyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter License Key");

        // Устанавливаем поле для ввода
        final EditText input = new EditText(this);
        builder.setView(input);

        // Устанавливаем кнопки
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String key = input.getText().toString().trim();
                // Проверяем корректность введенного лицензионного ключа
                if (checkLicenseKey(key)) {
                    // Если ключ верный, переходим на экран регистрации
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    finish(); // Закрываем текущий экран
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid license key", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Метод для проверки лицензионного ключа (нужно реализовать свою логику)
    private boolean checkLicenseKey(String key) {
        // Ваша логика проверки лицензионного ключа
        // Например, сравнение введенного ключа с предопределенным
        return key.equals("oaoao");
    }

    // Метод для валидации введенных данных
    private boolean validateInput(String username, String password) {
        return isValidUsername(username) && isValidPassword(password);
    }

    // Проверка валидности имени пользователя
    private boolean isValidUsername(String username) {
        // Здесь можно добавить свою логику проверки, например, наличие определенных символов и т.д.
        return username.length() > 0;
    }

    // Проверка валидности пароля (например, не менее 6 символов)
    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }
}
