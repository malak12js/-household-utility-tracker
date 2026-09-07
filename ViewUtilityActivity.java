package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class ViewUtilityActivity extends AppCompatActivity {

    ListView utilityListView;
    DatabaseHelper db;
    UtilityAdapter adapter;
    ArrayList<UtilityModel> utilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_utility);

        utilityListView = findViewById(R.id.utilityListView);
        db = new DatabaseHelper(this);

        utilities = db.getAllUtilities();

        if (utilities == null || utilities.isEmpty()) {
            Toast.makeText(this, "No utilities added yet!", Toast.LENGTH_SHORT).show();
        }

        adapter = new UtilityAdapter(this, utilities);
        utilityListView.setAdapter(adapter);
    }
}
