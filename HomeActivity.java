package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    TextView welcomeText, monthlyTotalText;
    Button addBtn, viewBtn, calcBtn, logoutBtn;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        welcomeText = findViewById(R.id.welcomeText);
        monthlyTotalText = findViewById(R.id.monthlyTotalText);
        addBtn = findViewById(R.id.addBtn);
        viewBtn = findViewById(R.id.viewBtn);
        calcBtn = findViewById(R.id.calcBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        db = new DatabaseHelper(this);

        updateMonthlyTotal();

        addBtn.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, AddUtilityActivity.class)));

        viewBtn.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ViewUtilityActivity.class)));

        calcBtn.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, UtilityCalculatorActivity.class)));

        // ✅ FIXED — replaced LoginActivity with LoginSignupActivity
        logoutBtn.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this,LoginSignUpActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMonthlyTotal();
    }

    private void updateMonthlyTotal() {
        double total;

        try {
            total = db.getMonthlyTotalForCurrentMonth();
        } catch (Exception e) {
            total = 0;
        }

        monthlyTotalText.setText("This Month: " + (int) total + " IQD");
    }
}
