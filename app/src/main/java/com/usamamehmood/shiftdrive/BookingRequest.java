package com.usamamehmood.shiftdrive;

//This class takes all the data needed for booking

public class BookingRequest {
    private String carImage;
    private String clientName;
    private String carMake;
    private String carModel;
    private String carDamage;
    private String noteForVendor;
    private String date;
    private String id;
    private String uid;
    private String time;
    private String bookingDate;

    public BookingRequest(String carImage, String clientName, String carMake, String carModel, String carDamage, String noteForVendor, String date, String id, String uid, String time, String bookingDate) {
        this.carImage = carImage;
        this.clientName = clientName;
        this.carMake = carMake;
        this.carModel = carModel;
        this.carDamage = carDamage;
        this.noteForVendor = noteForVendor;
        this.date = date;
        this.id = id;
        this.uid = uid;
        this.time = time;
        this.bookingDate = bookingDate;
    }

    public String getCarImage() {
        return carImage;
    }

    public void setCarImage(String carImage) {
        this.carImage = carImage;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCarMake() {
        return carMake;
    }

    public void setCarMake(String carMake) {
        this.carMake = carMake;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarDamage() {
        return carDamage;
    }

    public void setCarDamage(String carDamage) {
        this.carDamage = carDamage;
    }

    public String getNoteForVendor() {
        return noteForVendor;
    }

    public void setNoteForVendor(String noteForVendor) {
        this.noteForVendor = noteForVendor;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }
}
