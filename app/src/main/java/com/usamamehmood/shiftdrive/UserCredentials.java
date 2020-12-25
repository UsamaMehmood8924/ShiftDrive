package com.usamamehmood.shiftdrive;

public class UserCredentials {
    static String id = "";
    static String name = "";
    static String email = "";
    static String password = "";
    static String image = "";
    static String phone = "";

    public static String getPhone() {
        return phone;
    }

    public static void setPhone(String phone) {
        UserCredentials.phone = phone;
    }

    public UserCredentials() {
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        UserCredentials.id = id;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        UserCredentials.name = name;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        UserCredentials.email = email;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        UserCredentials.password = password;
    }

    public static String getImage() {
        return image;
    }

    public static void setImage(String image) {
        UserCredentials.image = image;
    }
}
