package com.example.saaga.healthplusplus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by saaga on 3/24/2018.
 */

public class Info extends Activity {
    private static EditText name;
    private static EditText age;
    private static EditText sex;
    private static EditText height;
    private static EditText weight;
    private static DatabaseReference databaseReference;

    public static String n, a, s, h, w;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        sex = findViewById(R.id.sex);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        databaseReference = FirebaseDatabase.getInstance().getReference();
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
        else if(v.getId() == R.id.update) {
            loadInfoToDatabase();
        }
        else if(v.getId() == R.id.request) {
            printInfoFromDatabase();
        }
    }

    public void printInfoFromDatabase() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //Toast.makeText(this, databaseReference.child(firebaseUser.getUid()).getDatabase().toString(), Toast.LENGTH_LONG);
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()) {
                    if(snap.getKey().equals(firebaseUser.getUid())) {
                        HashMap<String, Object> map = (HashMap<String, Object>) snap.getValue();
                        String name = map.get("name").toString();
                        String age = map.get("age").toString();
                        String gender = map.get("gender").toString().toUpperCase();
                        String height = map.get("height").toString();
                        String weight = map.get("weight").toString();
                        n = name; a = age; s = gender; h = height; w = weight;
//                        Info.name.setText(n);
//                        Info.age.setText(a);
//                        Info.sex.setText(s);
//                        Info.height.setText(h);
//                        Info.weight.setText(w);
                        Toast.makeText(Info.this, "Name: " + name + ", Age: " + age + ", Gender: " + gender +
                                ", Height: " + height + ", Weight: " + weight, Toast.LENGTH_LONG).show();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadInfoToDatabase() {
        String user = name.getText().toString().trim();
        String edad = age.getText().toString().trim();
        String gender = sex.getText().toString().trim().toUpperCase();
        String tall = height.getText().toString().trim();
        String fat = weight.getText().toString().trim();


        if(TextUtils.isEmpty(user) || TextUtils.isEmpty(edad) || TextUtils.isEmpty(gender) || TextUtils.isEmpty(tall)|| TextUtils.isEmpty(fat)) {
            Toast.makeText(this, "Please fill out everything", Toast.LENGTH_SHORT).show();
            return;
        }

        User client = new User(user, Integer.parseInt(edad), gender, Double.parseDouble(tall), Double.parseDouble(fat));
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child(firebaseUser.getUid()).setValue(client);
        Toast.makeText(this, "Information Saved", Toast.LENGTH_LONG).show();

    }
}
