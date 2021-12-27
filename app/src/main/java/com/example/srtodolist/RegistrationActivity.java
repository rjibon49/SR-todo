package com.example.srtodolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {
    private EditText registerEmail, registerPassword;
    private FirebaseAuth srAuth;

    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);

        Toolbar toolbar = findViewById(R.id.RegistrationToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sign Up");

        srAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);

        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        Button registerButton = findViewById(R.id.registerButton);
        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            Intent login = new Intent(RegistrationActivity.this, loginActivity.class);
            startActivity(login);
        });

        registerButton.setOnClickListener(v -> {
            String email = registerEmail.getText().toString().trim();
            String password = registerPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                registerEmail.setError("Email is required");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                registerPassword.setError("Password required");
            } else {
                loader.setMessage("Sign Up is progress");
                loader.setCanceledOnTouchOutside(false);
                loader.show();
                srAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this::onComplete);
            }
        });

    }

    private void onComplete(Task<AuthResult> task) {

        if (task.isSuccessful()) {
            Intent home = new Intent(RegistrationActivity.this, Todo.class);
            startActivity(home);
            finish();
        } else {
            Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
        }
        loader.dismiss();
    }
}