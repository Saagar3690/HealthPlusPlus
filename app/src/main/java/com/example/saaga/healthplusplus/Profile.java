package com.example.saaga.healthplusplus;

/**
 * Created by saaga on 3/24/2018.
 */

public interface Profile {
    String getName();
    String getGender();
    double getWeight();
    double getHeight();
    int getAge();
    void setName(String n);
    void setGender(String g);
    void setWeight(double w);
    void setHeight(double h);
    void setAge(int a);
}
