package com.example.saaga.healthplusplus;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Demo on 3/24/2018.
 */
public class FoodListItem {

    public static final Handler handler = new Handler();


    public boolean common;
    public String name;
    public Bitmap img;
    public HashMap<String, String> info = new HashMap<>();
    public ImageView iv;

    public static int images = 0;
    private static HashMap<String, Bitmap> image_cache = new HashMap<>();
    private static ArrayList<AsyncTask> tasks = new ArrayList<>();

    public static void reset() {
        images = 0;
        image_cache.clear();
        for(AsyncTask task : tasks) {
            task.cancel(true);
        }
        tasks.clear();
    }

    private void update() {
        if(iv != null) {
            iv.setImageBitmap(img);
        }
    }

    public void loadImage(final String src) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(image_cache.containsKey(src)) {
                    img = image_cache.get(src);
                }
                else {
                    images++;
                    try {
                        LoadImage task = new LoadImage();
                        tasks.add(task);
                        img = task.execute(src).get();
                        image_cache.put(src, img);
                        update();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public String toString() {
        return name;
    }
}

class LoadImage extends AsyncTask<String, Void, Bitmap> {

    protected Bitmap doInBackground(String... src) {
        if(isCancelled()) return null;
        try {
            URL url = new URL(src[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            return null;
        }
    }

    protected void onPostExecute(Bitmap b) {

    }
}