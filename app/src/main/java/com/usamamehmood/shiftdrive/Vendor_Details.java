package com.usamamehmood.shiftdrive;

//This is the mechanic's class

public class Vendor_Details {
    public static String name = "";
    public static String shopname = "";
    public static String id = "";
    public static String location = "";

    public Vendor_Details() {
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Vendor_Details.name = name;
    }

    public static String getShopname() {
        return shopname;
    }

    public static void setShopname(String shopname) {
        Vendor_Details.shopname = shopname;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        Vendor_Details.id = id;
    }

    public static String getLocation() {
        return location;
    }

    public static void setLocation(String location) {
        Vendor_Details.location = location;
    }
}
