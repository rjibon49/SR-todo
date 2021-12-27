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
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class loginActivity extends AppCompatActivity {
    private EditText loginEmail, loginPassword;

    private FirebaseAuth srAuth;
    private ProgressDialog loader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.loginToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sign In");

        srAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            Intent registration = new Intent(loginActivity.this, RegistrationActivity.class);
            startActivity(registration);
        });

        loginButton.setOnClickListener(v -> {
            String email = loginEmail.getText().toString().trim();
            String password = loginPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                loginEmail.setError("Email is required");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                loginPassword.setError("Password required");
            } else {
                loader.setMessage("Sign In progress");
                loader.setCanceledOnTouchOutside(false);
                loader.show();
                srAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Intent home = new Intent(loginActivity.this, Todo.class);
                        startActivity(home);
                        finish();
                    }
                    else {
                        Toast.makeText(loginActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                    }
                    loader.dismiss();
                });
            }
        });
    }
}