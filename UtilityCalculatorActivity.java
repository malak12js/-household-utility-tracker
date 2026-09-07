package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.util.TypedValue;
import android.widget.ImageView;
import android.view.Gravity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.ArrayList;

public class UtilityCalculatorActivity extends AppCompatActivity {

    DatabaseHelper db;
    LinearLayout container;
    TextView totalView;

    ArrayList<Long> rowIds = new ArrayList<>();
    ArrayList<EditText> rowEdits = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DatabaseHelper(this);

        ScrollView sv = new ScrollView(this);
        sv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = dpToPx(this, 16);
        container.setPadding(padding, padding, padding, padding);
        sv.addView(container);

        TextView title = new TextView(this);
        title.setText("Utility Prices — This Month");
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        title.setPadding(0,0,0,dpToPx(this,12));
        container.addView(title);

        loadUtilitiesForCurrentMonth();

        Button calcBtn = new Button(this);
        calcBtn.setText("Calculate Total");
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnLp.topMargin = dpToPx(this,12);
        container.addView(calcBtn, btnLp);

        totalView = new TextView(this);
        totalView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        totalView.setPadding(0, dpToPx(this, 12), 0, 0);
        double startingTotal = db.getMonthlyTotalForCurrentMonth();
        totalView.setText("This month utility expense is: " + (int) startingTotal + " IQD");
        container.addView(totalView);

        calcBtn.setOnClickListener(v -> calculateAndSave());

        setContentView(sv);

        computeDisplayedTotal();
    }

    private void loadUtilitiesForCurrentMonth() {
        Cursor cursor = db.getUtilitiesCursorForCurrentMonth();
        if (cursor == null) return;

        if (!cursor.moveToFirst()) {
            TextView empty = new TextView(this);
            empty.setText("No utilities for this month. Add utilities first.");
            empty.setPadding(0, dpToPx(this,8), 0, dpToPx(this,8));
            container.addView(empty);
            cursor.close();
            return;
        }

        do {
            long id = cursor.getLong(0);
            String type = cursor.getString(1);
            String currency = cursor.getString(2);
            String date = cursor.getString(3);
            String notes = cursor.getString(4);

            double existingPrice = db.getPriceForUtility(id);
            addRow(id, type, date, existingPrice, currency);

        } while (cursor.moveToNext());

        cursor.close();
    }

    private void addRow(long id, String type, String date, double price, String currency) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowLp.bottomMargin = dpToPx(this,8);
        row.setLayoutParams(rowLp);
        row.setGravity(Gravity.CENTER_VERTICAL);

        ImageView icon = new ImageView(this);
        int iconSize = dpToPx(this, 36);
        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(iconSize, iconSize);
        iconLp.rightMargin = dpToPx(this, 12);
        icon.setLayoutParams(iconLp);
        icon.setImageResource(getIconForType(type));

        TextView label = new TextView(this);
        label.setText(type + " (" + date + ")");
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        LinearLayout.LayoutParams labelLp = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        label.setLayoutParams(labelLp);

        EditText edit = new EditText(this);
        edit.setHint("Amount");
        edit.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edit.setText(price == 0 ? "" : String.valueOf((int) price));
        LinearLayout.LayoutParams editLp = new LinearLayout.LayoutParams(dpToPx(this,100),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        edit.setLayoutParams(editLp);
        edit.setGravity(Gravity.END);

        row.addView(icon);
        row.addView(label);
        row.addView(edit);

        container.addView(row);

        rowIds.add(id);
        rowEdits.add(edit);
    }

    private void calculateAndSave() {
        double total = 0;
        boolean anyUpdated = false;

        for (int i = 0; i < rowIds.size(); i++) {
            long id = rowIds.get(i);
            EditText edit = rowEdits.get(i);
            String s = edit.getText().toString().trim();
            double val = 0;
            if (!s.isEmpty()) {
                try { val = Double.parseDouble(s); } catch (Exception e) { val = 0; }
            }
            boolean ok = db.savePriceForUtility(id, val);
            if (ok) anyUpdated = true;
            total += val;
        }

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        totalView.setText("This month utility expense is: " + (int) total + " IQD");

        if (anyUpdated) {
            Toast.makeText(this, "Prices saved. Total: " + (int) total + " IQD", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Total: " + (int) total + " IQD", Toast.LENGTH_SHORT).show();
        }
    }

    private void computeDisplayedTotal() {
        double total = 0;
        for (EditText e : rowEdits) {
            String s = e.getText().toString().trim();
            if (!s.isEmpty()) {
                try { total += Double.parseDouble(s); }
                catch (Exception ignored) {}
            }
        }
        totalView.setText("This month utility expense is: " + (int) total + " IQD");
    }

    private int getIconForType(String type) {
        if (type == null) return R.drawable.ic_home;
        String t = type.toLowerCase();
        if (t.contains("water")) return R.drawable.ic_water_drop;
        if (t.contains("electric") || t.contains("bolt") || t.contains("power")) return R.drawable.ic_bolt;
        if (t.contains("gas") || t.contains("fire")) return R.drawable.ic_local_fire;
        if (t.contains("internet") || t.contains("wifi")) return R.drawable.ic_wifi;
        return R.drawable.ic_home;
    }

    private int dpToPx(Context c, int dp) {
        float density = c.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
