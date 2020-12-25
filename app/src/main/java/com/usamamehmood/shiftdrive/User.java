package com.usamamehmood.shiftdrive;

public class User {
    String name;
    String phone;
    String email;
    String password;
    String image;
    String id;

    public User() {
    }

    public User(String name, String phone, String email, String password, String image, String id) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.image = image;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
