package com.example.saaga.healthplusplus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Demo on 3/25/2018.
 */
public class FoodListAdapter extends BaseAdapter {

    private List<FoodListItem> items;
    private LayoutInflater inflater;
    private Context context;

    public FoodListAdapter(Context context, List<FoodListItem> items) {
        this.items = items;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.list_item, null);
        TextView text = (TextView) vi.findViewById(R.id.title_name);
        ImageView img = (ImageView) vi.findViewById(R.id.thumb_icon);
        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nutrition act = (Nutrition) context;
                if(items.get(position).common)
                    act.requestCommon(items.get(position));
                else {
                    act.addFood(Food.fromItem(items.get(position)));
                    act.updateTracked();
                }
            }
        });
        text.setText(items.get(position).name);
        if(items.get(position).img != null) {
            items.get(position).iv = img;
            img.setImageBitmap(items.get(position).img);
        }
        return vi;
    }
}
