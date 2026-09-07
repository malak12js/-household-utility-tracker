package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "utilities.db";

    // 🔥 FIXED → bumped version so tables rebuild
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_UTILITIES = "utilities";
    public static final String COL_UTIL_ID = "id";
    public static final String COL_TYPE = "type";
    public static final String COL_CURRENCY = "currency";
    public static final String COL_DATE = "date";
    public static final String COL_NOTES = "notes";

    public static final String TABLE_PRICES = "utility_prices";
    public static final String COL_PRICE_ID = "id";
    public static final String COL_PRICE_UTIL_ID = "utility_id";
    public static final String COL_PRICE_AMOUNT = "amount";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_UTILITIES + " (" +
                COL_UTIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TYPE + " TEXT, " +
                COL_CURRENCY + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_NOTES + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_PRICES + " (" +
                COL_PRICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PRICE_UTIL_ID + " INTEGER UNIQUE, " +
                COL_PRICE_AMOUNT + " REAL, " +
                "FOREIGN KEY(" + COL_PRICE_UTIL_ID + ") REFERENCES " +
                TABLE_UTILITIES + "(" + COL_UTIL_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRICES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UTILITIES);
        onCreate(db);
    }

    // Add a utility
    public boolean addUtility(String type, String currency, String date, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TYPE, type);
        cv.put(COL_CURRENCY, currency);
        cv.put(COL_DATE, date);
        cv.put(COL_NOTES, notes);
        long id = db.insert(TABLE_UTILITIES, null, cv);
        db.close();
        return id != -1;
    }

    // Get all utilities for current month
    public Cursor getUtilitiesCursorForCurrentMonth() {
        SQLiteDatabase db = this.getReadableDatabase();

        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        String pattern = "%/" + month + "/" + year + "%";

        return db.rawQuery(
                "SELECT " + COL_UTIL_ID + ", " +
                        COL_TYPE + ", " +
                        COL_CURRENCY + ", " +
                        COL_DATE + ", " +
                        COL_NOTES +
                        " FROM " + TABLE_UTILITIES +
                        " WHERE " + COL_DATE + " LIKE ?",
                new String[]{ pattern }
        );
    }

    // Monthly total calculation
    public double getMonthlyTotalForCurrentMonth() {
        SQLiteDatabase db = this.getReadableDatabase();

        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        String pattern = "%/" + month + "/" + year + "%";

        Cursor c = db.rawQuery(
                "SELECT SUM(p." + COL_PRICE_AMOUNT + ") " +
                        "FROM " + TABLE_UTILITIES + " u " +
                        "LEFT JOIN " + TABLE_PRICES + " p " +
                        "ON u." + COL_UTIL_ID + " = p." + COL_PRICE_UTIL_ID +
                        " WHERE u." + COL_DATE + " LIKE ?",
                new String[]{ pattern }
        );

        double total = 0;
        if (c.moveToFirst() && !c.isNull(0)) {
            total = c.getDouble(0);
        }

        c.close();
        db.close();
        return total;
    }

    // Save or update price
    public boolean savePriceForUtility(long utilityId, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id FROM " + TABLE_PRICES + " WHERE " + COL_PRICE_UTIL_ID + "=?",
                new String[]{ String.valueOf(utilityId) });

        ContentValues cv = new ContentValues();
        cv.put(COL_PRICE_UTIL_ID, utilityId);
        cv.put(COL_PRICE_AMOUNT, amount);

        boolean ok;
        if (c.moveToFirst()) {
            ok = db.update(TABLE_PRICES, cv,
                    COL_PRICE_UTIL_ID + "=?",
                    new String[]{ String.valueOf(utilityId) }) > 0;
        } else {
            ok = db.insert(TABLE_PRICES, null, cv) != -1;
        }

        c.close();
        db.close();
        return ok;
    }

    // Get price for one utility
    public double getPriceForUtility(long utilityId) {
        double amount = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT " + COL_PRICE_AMOUNT +
                        " FROM " + TABLE_PRICES +
                        " WHERE " + COL_PRICE_UTIL_ID + "=?",
                new String[]{ String.valueOf(utilityId) });

        if (c.moveToFirst()) {
            amount = c.getDouble(0);
        }

        c.close();
        db.close();
        return amount;
    }

    // Clear everything
    public void clearAllUtilitiesAndPrices() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRICES, null, null);
        db.delete(TABLE_UTILITIES, null, null);
        db.close();
    }

    // Get all utilities
    public ArrayList<UtilityModel> getAllUtilities() {
        ArrayList<UtilityModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT " + COL_UTIL_ID + ", " + COL_TYPE + ", " + COL_CURRENCY + ", " + COL_DATE + ", " + COL_NOTES +
                        " FROM " + TABLE_UTILITIES +
                        " ORDER BY " + COL_UTIL_ID + " DESC",
                null
        );

        if (c.moveToFirst()) {
            do {
                long id = c.getLong(0);
                String type = c.getString(1);
                String currency = c.getString(2);
                String date = c.getString(3);
                String notes = c.getString(4);

                double price = getPriceForUtility(id);

                list.add(new UtilityModel(id, type, currency, date, notes, price));

            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return list;
    }
}
