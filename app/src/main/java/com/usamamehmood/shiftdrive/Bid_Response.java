package com.usamamehmood.shiftdrive;

//This class takes the response from vendor

public class Bid_Response {
    private String id;
    private String vendor_id;
    private String shopname;
    private String shop_location;
    private String makemodel;
    private String date;
    private String time;
    private String damage;
    private String image;
    private String noteforvendor;
    private String notefromvendor;
    private String price;

    public Bid_Response(String id, String vendor_id, String shopname, String shop_location, String makemodel, String date, String time, String damage, String image, String noteforvendor, String notefromvendor, String price) {
        this.id = id;
        this.vendor_id = vendor_id;
        this.shopname = shopname;
        this.shop_location = shop_location;
        this.makemodel = makemodel;
        this.date = date;
        this.time = time;
        this.damage = damage;
        this.image = image;
        this.noteforvendor = noteforvendor;
        this.notefromvendor = notefromvendor;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(String vendor_id) {
        this.vendor_id = vendor_id;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getShop_location() {
        return shop_location;
    }

    public void setShop_location(String shop_location) {
        this.shop_location = shop_location;
    }

    public String getMakemodel() {
        return makemodel;
    }

    public void setMakemodel(String makemodel) {
        this.makemodel = makemodel;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDamage() {
        return damage;
    }

    public void setDamage(String damage) {
        this.damage = damage;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNoteforvendor() {
        return noteforvendor;
    }

    public void setNoteforvendor(String noteforvendor) {
        this.noteforvendor = noteforvendor;
    }

    public String getNotefromvendor() {
        return notefromvendor;
    }

    public void setNotefromvendor(String notefromvendor) {
        this.notefromvendor = notefromvendor;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
