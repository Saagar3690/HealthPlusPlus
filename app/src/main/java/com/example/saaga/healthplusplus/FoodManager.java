package com.example.saaga.healthplusplus;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Demo on 3/24/2018.
 */
public class FoodManager extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Food> items;

    public FoodManager(Context context, List<Food> items) {
        this.context = context;
        this.items = items;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void add(String name, HashMap<String, String> data) {
        Food f = new Food();
        f.name = name;
        f.nutrition.putAll(data);
        items.add(f);
        notifyDataSetChanged();
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
    public View getView(int position, final View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.meal_item, null);
        TextView title = (TextView) vi.findViewById(R.id.meal_name);
        TextView cal = (TextView) vi.findViewById(R.id.meal_calories);
        final Food item = items.get(position);
        ImageButton rem = (ImageButton) vi.findViewById(R.id.remove_meal);
        rem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nutrition n = (Nutrition) context;
                n.removeFood(item);
            }
        });
        title.setText(item.name);
        title.setTextSize(20);
        cal.setText(String.valueOf((int) item.getCalories() + " cal"));
        cal.setTextColor(Color.argb(255, 55, 255, 120));
        cal.setTextSize(16);
        return vi;
    }
}

class Food {
    public String name;
    public HashMap<String, String> nutrition = new HashMap<>();

    public static Food fromItem(FoodListItem item) {
        Food f = new Food();
        f.name = item.name;
        f.nutrition = item.info;
        return f;
    }

    public double getCalories() {
        return Double.parseDouble(nutrition.get("calories"));//nutrition.get("carbohydrates") * 4 + nutrition.get("protein") * 4 + nutrition.get("total fat") * 9;
    }
}
