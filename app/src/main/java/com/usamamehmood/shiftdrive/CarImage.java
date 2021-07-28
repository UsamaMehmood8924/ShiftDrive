package com.usamamehmood.shiftdrive;

import android.net.Uri;

//This class takes teh car image

public class CarImage {
    public static Uri img=null;

    public static Uri getImg() {
        return img;
    }

    public static void setImg(Uri img) {
        CarImage.img = img;
    }

    public CarImage() {
    }
}
