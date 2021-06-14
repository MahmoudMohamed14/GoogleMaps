package com.example.googlemaps;

public class MechanicInfo {
    private String email, phone ,id ,password,image,rating,name,service;


    public MechanicInfo() {

    }


    public MechanicInfo(String email, String phone, String id, String password, String image, String rating, String name, String service) {
        this.email = email;
        this.phone = phone;
        this.id = id;
        this.password = password;
        this.image = image;
        this.rating = rating;
        this.name = name;
        this.service = service;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
