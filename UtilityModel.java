package com.example.myapplication;

public class UtilityModel {
    private long id;
    private String type;
    private String currency;
    private String date;
    private String notes;
    private double price;

    public UtilityModel(long id, String type, String currency, String date, String notes, double price) {
        this.id = id;
        this.type = type;
        this.currency = currency;
        this.date = date;
        this.notes = notes;
        this.price = price;
    }

    public long getId() { return id; }
    public String getType() { return type; }
    public String getCurrency() { return currency; }
    public String getDate() { return date; }
    public String getNotes() { return notes; }
    public double getPrice() { return price; }
}
