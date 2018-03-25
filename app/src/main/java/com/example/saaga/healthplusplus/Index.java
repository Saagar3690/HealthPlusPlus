package com.example.saaga.healthplusplus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;


import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

/**
 * Created by saaga on 3/24/2018.
 */

public class Index extends Activity {
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        System.out.print(user);

        //Info.printInfoFromDatabase();

        TextView def = (TextView) findViewById(R.id.def);
        int cal = (int) -(10 * Double.parseDouble("120")/2.2 + 6.25 * Double.parseDouble("65")*2.54 - 5 * Double.parseDouble("15") + 5);
        for(ExerciseItem item : Fitness.exerciseItems) {
            cal -= item.calories;
        }
        for(Food item : Nutrition.foods) {
            cal += item.getCalories();//Double.parseDouble(item.getCalories()) / Double.parseDouble(item.info.get("serving_qty"));
        }
        def.setText(String.valueOf(cal) + " (" + cal/3500.0 + "lb)");

        double minu = cal / (.2 * 125/2.2);
        double minuBike = cal / (405/60.0);

        if(minu > 10) {
            TextView sug = (TextView) findViewById(R.id.sug);
            sug.setText("Suggestion: Run for " + Math.round(minu*100)/100.0 + " minutes to burn off those extra calories, or \nBike for " + Math.round(minuBike*100)/100.0 + " minutes");
        }


        TextView com = (TextView) findViewById(R.id.comment);
        if(cal < -500) {
            com.setText("Eat more food! Undernourishment is not healthy.");
        }
        else if(cal > 500) {
            com.setText("Exercise more. You need to burn the calories gained from eating food.");
        }
        else {
            com.setText("Great job having a balanced life. You are eating the right amount of healthy food and doing sufficient exercise.");
        }
    }

    public void logOut(View v) {
        if(v.getId() == R.id.logout) {
            if (firebaseAuth != null) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
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
