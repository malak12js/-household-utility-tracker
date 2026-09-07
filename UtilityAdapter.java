package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class UtilityAdapter extends BaseAdapter {

    Context context;
    ArrayList<UtilityModel> list;

    public UtilityAdapter(Context context, ArrayList<UtilityModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override public int getCount() { return list.size(); }
    @Override public Object getItem(int i) { return list.get(i); }
    @Override public long getItemId(int i) { return list.get(i).getId(); }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.utility_item, parent, false);
        }

        UtilityModel m = list.get(pos);

        TextView tType = convertView.findViewById(R.id.utilityType);
        TextView tDate = convertView.findViewById(R.id.utilityDate);
        TextView tPrice = convertView.findViewById(R.id.utilityPrice);
        TextView tNotes = convertView.findViewById(R.id.utilityNotes);

        tType.setText(m.getType());
        tDate.setText(m.getDate());
        tPrice.setText((int)m.getPrice() + " " + m.getCurrency());
        tNotes.setText(m.getNotes().isEmpty() ? "No notes" : m.getNotes());

        return convertView;
    }
}
