package com.example.saaga.healthplusplus;

/**
 * Created by saaga on 3/24/2018.
 */

public class User implements Profile {
    private String name;
    private String gender;
    private int age;
    private double height;
    private double weight;

    public User() {
        name = "";
        gender = "";
        age = 0;
        height = weight = 0.0;
    }

    public User(String n, int a, String g, double h, double w) {
        name = n;
        age = a;
        gender = g;
        height = h;
        weight = w;
    }

    public String getName() { return name; }
    public String getGender() { return gender; }
    public double getWeight() { return weight; }
    public double getHeight() { return height; }
    public int getAge() { return age; }
    public void setName(String n) { name = n; }
    public void setGender(String g) { gender = g; }
    public void setWeight(double w) { weight = w; }
    public void setHeight(double h) { height = h; }
    public void setAge(int a) { age = a; }
}
