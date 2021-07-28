package com.usamamehmood.shiftdrive;

import android.net.Uri;

//This class takes the car details

public class CarDetails {
    public static String make_model = "";
    public static Uri damagedCarImg = null;

    public CarDetails() {
    }

    public static String getMake_model() {
        return make_model;
    }

    public static void setMake_model(String make_model) {
        CarDetails.make_model = make_model;
    }

    public static Uri getDamagedCarImg() {
        return damagedCarImg;
    }

    public static void setDamagedCarImg(Uri damagedCarImg) {
        CarDetails.damagedCarImg = damagedCarImg;
    }
}
