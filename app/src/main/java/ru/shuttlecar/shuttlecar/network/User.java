package ru.shuttlecar.shuttlecar.network;

public class User {
    private int id;
    private String un_id;
    private String image_person;
    private String email;
    private String name;
    private String uiu;
    private String image_car;
    private String tel;
    private String car_brand;
    private String car_model;
    private float rating;

    private String password;
    private String old_password;
    private String new_password;

    public int getId() {
        return id;
    }

    public String getUnique_id() {
        return un_id;
    }

    public String getImage_person() {
        return image_person;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getUiu() {
        return uiu;
    }

    public String getImage_car() {
        return image_car;
    }

    public String getTel() {
        return tel;
    }

    public String getCar_brand() {
        return car_brand;
    }

    public String getCar_model() {
        return car_model;
    }

    public float getRating() {
        return rating;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUnique_id(String un_id) {
        this.un_id = un_id;
    }

    public void setImage_person(String image_person) {
        this.image_person = image_person;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage_car(String image_car) {
        this.image_car = image_car;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setCar_brand(String car_brand) {
        this.car_brand = car_brand;
    }

    public void setCar_model(String car_model) {
        this.car_model = car_model;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public void setOld_password(String old_password) {
        this.old_password = old_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }
}
