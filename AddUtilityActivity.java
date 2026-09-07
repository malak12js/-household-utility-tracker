package com.example.myapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddUtilityActivity extends AppCompatActivity {

    TextView typeSelector, dateSelector, currencySelector;
    EditText notesInput;
    Button addUtilityBtn, nextBtn;

    String selectedType = "";
    String selectedDate = "";
    String selectedCurrency = "IQD"; // default

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_utility);

        db = new DatabaseHelper(this);

        typeSelector = findViewById(R.id.typeSelector);
        dateSelector = findViewById(R.id.dateSelector);
        currencySelector = findViewById(R.id.currencySelector);
        notesInput = findViewById(R.id.notesInput);
        addUtilityBtn = findViewById(R.id.addUtilityBtn);
        nextBtn = findViewById(R.id.nextBtn);

        typeSelector.setText("Select Utility Type");
        currencySelector.setText("Currency (IQD)");
        dateSelector.setText("Select Date");

        typeSelector.setOnClickListener(v -> showTypeDialog());
        dateSelector.setOnClickListener(v -> showDatePicker());
        currencySelector.setOnClickListener(v -> showCurrencyDialog());

        addUtilityBtn.setOnClickListener(v -> saveUtility());

        // ✅ FIX: Next button goes to UtilityCalculatorActivity
        nextBtn.setOnClickListener(v ->
                startActivity(new Intent(AddUtilityActivity.this, UtilityCalculatorActivity.class))
        );
    }

    private void showTypeDialog() {
        String[] types = {"Electricity", "Water", "Internet", "Gas", "Other"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Choose Utility Type");
        dialog.setItems(types, (d, i) -> {
            selectedType = types[i];
            typeSelector.setText(selectedType);
        });
        dialog.show();
    }

    private void showCurrencyDialog() {
        String[] currencies = {"IQD", "USD"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Choose Currency");
        dialog.setItems(currencies, (d, i) -> {
            selectedCurrency = currencies[i];
            currencySelector.setText(selectedCurrency);
        });
        dialog.show();
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(
                this,
                (view, y, m, d) -> {
                    selectedDate = d + "/" + (m + 1) + "/" + y;
                    dateSelector.setText(selectedDate);
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        dp.show();
    }

    private void saveUtility() {
        String notes = notesInput.getText().toString().trim();

        if (selectedType.isEmpty()) {
            Toast.makeText(this, "Please select a utility type", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean inserted = db.addUtility(selectedType, selectedCurrency, selectedDate, notes);

        if (inserted) {
            Toast.makeText(this, "Utility added!", Toast.LENGTH_SHORT).show();

            // Reset fields to add more utilities
            selectedType = "";
            selectedDate = "";
            selectedCurrency = "IQD";

            typeSelector.setText("Select Utility Type");
            dateSelector.setText("Select Date");
            currencySelector.setText("Currency (IQD)");
            notesInput.setText("");
        } else {
            Toast.makeText(this, "Error saving utility!", Toast.LENGTH_SHORT).show();
        }
    }
}
