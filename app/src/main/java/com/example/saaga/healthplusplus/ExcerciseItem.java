package com.example.saaga.healthplusplus;

/**
 * Created by saaga on 3/25/2018.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;


class ExerciseItemAdapter extends BaseAdapter {

    private Context context;
    private List<ExerciseItem> items;
    private LayoutInflater li;

    public ExerciseItemAdapter(Context contextIn, List<ExerciseItem> itemsIn) {
        context = contextIn;
        items = itemsIn;
        li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = li.inflate(R.layout.exercise_item, null);
        TextView title = (TextView) vi.findViewById(R.id.exercise_name);
        TextView cal = (TextView) vi.findViewById(R.id.burned_calories);
        ExerciseItem item = items.get(position);
        title.setText(item.name);
        title.setTextSize(20);
        cal.setText(String.valueOf(item.calories + " cal"));
        cal.setTextColor(Color.argb(255, 55, 255, 120));
        cal.setTextSize(16);
        return vi;
    }
}