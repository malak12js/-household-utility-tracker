package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginSignUpActivity extends AppCompatActivity {

    // Layouts
    LinearLayout loginLayout, signUpLayout;

    // Login UI
    EditText loginUsername, loginPassword;
    Button loginBtn;

    // Signup UI
    EditText signupUsername, signupPassword, signupConfirm;
    Button signupBtn;

    // Switch buttons
    Button showLoginBtn, showSignUpBtn;

    UserDatabase userDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);

        userDb = new UserDatabase(this);

        // Layouts
        loginLayout = findViewById(R.id.loginLayout);
        signUpLayout = findViewById(R.id.signUpLayout);

        // Buttons to switch pages
        showLoginBtn = findViewById(R.id.showLoginBtn);
        showSignUpBtn = findViewById(R.id.showSignUpBtn);

        // Login controls
        loginUsername = findViewById(R.id.loginUsername);
        loginPassword = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);

        // Signup controls
        signupUsername = findViewById(R.id.signupUsername);
        signupPassword = findViewById(R.id.signupPassword);
        signupConfirm = findViewById(R.id.signupConfirm);
        signupBtn = findViewById(R.id.signupBtn);

        // Switch layouts
        showLoginBtn.setOnClickListener(v -> showLogin());
        showSignUpBtn.setOnClickListener(v -> showSignUp());

        // Login
        loginBtn.setOnClickListener(v -> loginUser());

        // Signup
        signupBtn.setOnClickListener(v -> signupUser());
    }

    // Slide to login page
    private void showLogin() {
        loginLayout.animate().translationX(0).setDuration(300);
        signUpLayout.animate().translationX(1000).setDuration(300);
    }

    // Slide to signup page
    private void showSignUp() {
        loginLayout.animate().translationX(-1000).setDuration(300);
        signUpLayout.animate().translationX(0).setDuration(300);
    }

    private void loginUser() {
        String user = loginUsername.getText().toString().trim();
        String pass = loginPassword.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userDb.checkUser(user, pass)) {

            // RESET DATABASE FOR NEW USER
            DatabaseHelper db = new DatabaseHelper(this);
            db.clearAllUtilitiesAndPrices();

            // Save login
            SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
            sp.edit().putString("username", user).apply();

            Toast.makeText(this, "Login successfully", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, HomeActivity.class));
            finish();

        } else {
            Toast.makeText(this, "Wrong username or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void signupUser() {
        String user = signupUsername.getText().toString().trim();
        String pass = signupPassword.getText().toString().trim();
        String confirm = signupConfirm.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(confirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userDb.isUsernameTaken(user)) {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userDb.addUser(user, pass)) {

            // RESET DATABASE FOR NEW USER
            DatabaseHelper db = new DatabaseHelper(this);
            db.clearAllUtilitiesAndPrices();

            Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();

            // Save login
            SharedPreferences sp = getSharedPreferences("MyApp", MODE_PRIVATE);
            sp.edit().putString("username", user).apply();

            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }
}
