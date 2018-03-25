package com.example.saaga.healthplusplus;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by saaga on 3/24/2018.
 */

public class Nutrition extends Activity {

    private String code;
    private FoodManager fm;
    private RequestQueue queue;
    public static List<FoodListItem> foodList = new LinkedList<>();
    public static List<Food> foods = new ArrayList<>();
    static {
        Food f = new Food();
        f.name = "Mozzarella Cheese";
        f.nutrition.put("calories", "78");
        foods.add(f);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(Nutrition.this, "Permission granted for camera", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(Nutrition.this, "Permission denied for camera", Toast.LENGTH_SHORT).show();
                return;
            }
            case 13: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(Nutrition.this, "Permission granted for reading", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(Nutrition.this, "Permission denied for reading", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void requestPermissions() {
        //ActivityCompat.requestPermissions(Nutrition.this, new String[] { Manifest.permission.CAMERA }, 1);
        ActivityCompat.requestPermissions(Nutrition.this, new String[] { android.Manifest.permission.READ_EXTERNAL_STORAGE }, 13);
    }


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nutrition);

        requestPermissions();

        queue = Volley.newRequestQueue(this);

        fm = new FoodManager(this, foods);
        updateTracked();

        Button capture = (Button) findViewById(R.id.scan);
        capture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                scan2();
            }
        });

        final EditText search = (EditText) findViewById(R.id.search);
        assert search != null;
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() <= 1) return;
                String url = "https://trackapi.nutritionix.com/v2/search/instant?query=" + search.getText().toString();
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    parseSearch(response);
                                }
                                catch(IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Nutrition.this, "It didn't work", Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    public HashMap<String, String> getHeaders() {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("x-app-id", "9b48539a");
                        map.put("x-app-key", "c5082f3032ec68c9540d73fb8d923392\n");
                        return map;
                    }

                };

                queue.add(stringRequest);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void sendRequest() {

        String url = "https://api.nutritionix.com/v1_1/item?upc=" + code + "&appId=" + "9b48539a" + "&appKey=" + "c5082f3032ec68c9540d73fb8d923392";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            parseUPC(response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //Toast.makeText(Nutrition.this, "Response is: "+ response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Nutrition.this, "It didn't work", Toast.LENGTH_LONG).show();
            }
        });

        queue.add(stringRequest);
    }

    public void requestCommon(final FoodListItem food) {
        String url = "https://trackapi.nutritionix.com/v2/natural/nutrients";//https://api.nutritionix.com/v1_1/item?upc=" + code + "&appId=" + "bafc9805" + "&appKey=" + "8924662476b42e67193db2039f77a9df";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            parseCommon(response, food);
                            addFood(Food.fromItem(food));
                            updateTracked();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Nutrition.this, "It didn't work", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            public Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                String q = "";
                params.put("query", q + food.name + q);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> map = new HashMap<>();
                //map.put("Content-Type", "application/json");
                map.put("x-app-id", "9b48539a");
                map.put("x-app-key", "c5082f3032ec68c9540d73fb8d923392");
                return map;
            }
        };

        queue.add(stringRequest);
    }

    private void parseCommon(String data, FoodListItem food) throws IOException {
        JsonReader in = new JsonReader(new StringReader(data));
        in.setLenient(true);
        in.beginObject();
        in.nextName();
        in.beginArray();
        in.beginObject();
        String cal = "";
        String qty = "";
        while(in.hasNext()) {
            String name = in.nextName();
            if(in.peek() != JsonToken.NUMBER && in.peek() != JsonToken.STRING) {
                in.skipValue();
            }
            else {
                String value = in.nextString();
                System.out.println(name + ", " + value);
                if(name.equals("nf_calories")) {
                    cal = value;
                }
                else if(name.equals("serving_qty")) {
                    qty = value;
                }
            }
        }
        Log.e("new INFOL ", cal + " " + qty);
        food.info.put("calories", cal);
        food.info.put("serving_qty", qty);
        in.close();
    }

    private void parseSearch(String data) throws IOException {
        foodList.clear();
        FoodListItem.reset();
        JsonReader in = new JsonReader(new StringReader(data));
        in.setLenient(true);
        in.beginObject();
        System.out.println(data.substring(data.indexOf("common")));
        while(in.hasNext()) {
            String type = in.nextName();
            int count = 0;
            if(type.equals("self")) in.skipValue();
            else {
                in.beginArray();
                while(in.hasNext()) {
                    if(count >= 6) {
                        in.skipValue();
                        continue;
                    }
                    HashMap<String, String> info = new HashMap<>();
                    FoodListItem item = new FoodListItem();
                    String foodName = "";
                    in.beginObject();
                    while(in.hasNext()) {
                        String attr = in.nextName();
                        String value = null;
                        if(in.peek() == JsonToken.NULL) {
                            in.skipValue();
                            continue;
                        }
                        else if(in.peek() == JsonToken.BEGIN_OBJECT) {
                            if(attr.equals("photo")) {
                                in.beginObject();
                                while(in.hasNext()) {
                                    String a2 = in.nextName();
                                    if(in.peek() != JsonToken.STRING) {
                                        in.skipValue();
                                        continue;
                                    }
                                    if(a2.equals("thumb")) {
                                        info.put("image", in.nextString());
                                    }
                                }
                                in.endObject();
                            }
                            else in.skipValue();
                        }
                        else {
                            value = in.nextString();
                        }
                        if(attr.equals("food_name")) {
                            foodName = value;
                        }
                        else if(attr.startsWith("nf_")) {
                            info.put(attr.substring(3), value);
                        }
                        else {
                            info.put(attr, value);
                        }
                    }
                    in.endObject();
                    item.name = foodName;
                    if(type.equals("branded"))
                        foodList.add(item);
                    else {
                        item.common = true;
                        foodList.add(count, item);
                    }
                    double cal = !info.containsKey("calories") ? 0 : Double.parseDouble(info.get("calories"));
                    double serve = !info.containsKey("serving_qty") ? 1 : Double.parseDouble(info.get("serving_qty"));
                    System.out.println("Calories: " + cal + ", Servings: " + serve);
                    info.put("calories", String.valueOf(cal / serve));
                    info.put("serving_qty", "1");
                    item.info = info;
                    if(info.containsKey("image") && !info.get("image").equals("null") && FoodListItem.images++ < 20) {
                        item.loadImage(info.get("image"));
                    }
                    count++;
                }
                in.endArray();
            }
        }
        in.endObject();
        updateList();

    }

    private void updateList() {
        ListView v = (ListView) findViewById(R.id.listView);
        //final ArrayAdapter<FoodListItem> arrayAdapter = new ArrayAdapter<FoodListItem>(this, android.R.layout.simple_list_item_1, foodList);
        FoodListAdapter adapter = new FoodListAdapter(this, foodList);
        v.setAdapter(adapter);
    }
    public void addFood(Food f) {
        foods.add(f);
        fm.notifyDataSetChanged();
    }
    public void removeFood(Food f) {
        foods.remove(f);
        fm.notifyDataSetChanged();
    }
    public void updateTracked() {
        ListView v = (ListView) findViewById(R.id.listView);
        v.setAdapter(fm);
    }

    private String parseUPC(String data) throws IOException {
        JsonReader in = new JsonReader(new StringReader(data));
        in.setLenient(true);
        in.beginObject();
        HashMap<String, String> info = new HashMap<>();
        String foodName = "";
        String stuff = "";
        while(in.hasNext()) {
            String name = in.nextName();
            String value = null;
            if(in.peek() == JsonToken.NULL) in.nextNull();
            else value = in.nextString();
            if(name.startsWith("nf_")) {
                info.put(name.substring(3), value);
            }
            if(name.equals("item_name")) {
                foodName += value;
            }
            if(name.equals("brand_name")) {
                foodName = value + " - " + foodName;
            }
        }
        in.endObject();
        fm.add(foodName, info);
        updateTracked();
        return stuff;
    }

    private void scan2() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            Toast.makeText(this, "Scanned Code 2: " + scanResult.getContents(), Toast.LENGTH_LONG).show();
            code = scanResult.getContents();
            sendRequest();
            return;
        }
    }

    public void redirect(View v) {
        if(v.getId() == R.id.home) {
            startActivity(new Intent(getApplicationContext(), Index.class));
        }
        else if(v.getId() == R.id.info) {
            startActivity(new Intent(getApplicationContext(), Info.class));
        }
        else if(v.getId() == R.id.fitness) {
            startActivity(new Intent(getApplicationContext(), Fitness.class));
        }
        else if(v.getId() == R.id.nutrition) {
            startActivity(new Intent(getApplicationContext(), Nutrition.class));
        }
    }
}

