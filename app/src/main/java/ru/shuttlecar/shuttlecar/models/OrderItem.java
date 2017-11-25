package ru.shuttlecar.shuttlecar.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;

public class OrderItem implements Parcelable {
    private int id;
    private String[] places;
    private String luggage_size;
    private int count_place;
    private int count_pass;
    private String date;
    private String time;

    private String name;
    private String tel;
    private String image_person;
    private float rating;

    private String image_car;
    private String car_brand;
    private String car_model;

    private ArrayList<PassengerItem> passengers;
    private boolean reserve;

    public OrderItem(int id, String[] places, String luggage_size, int count_place, String date, String time) {
        this.id = id;
        this.places = places;
        this.luggage_size = luggage_size;
        this.count_place = count_place;
        this.date = date;
        this.time = time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPlaces(String[] places) {
        this.places = places;
    }

    public void setLuggage_size(String luggage_size) {
        this.luggage_size = luggage_size;
    }

    public void setCount_place(int count_place) {
        this.count_place = count_place;
    }

    public void setCount_pass(int count_pass) {
        this.count_pass = count_pass;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setImage_person(String image_person) {
        this.image_person = image_person;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setImage_car(String image_car) {
        this.image_car = image_car;
    }

    public void setCar_brand(String car_brand) {
        this.car_brand = car_brand;
    }

    public void setCar_model(String car_model) {
        this.car_model = car_model;
    }

    public void setPassengers(ArrayList<PassengerItem> passengers) {
        this.passengers = passengers;
    }

    public void setReserve(boolean reserve) {
        this.reserve = reserve;
    }

    public int getId() {
        return id;
    }

    public String[] getPlaces() {
        return places;
    }

    public String getLuggage_size() {
        return luggage_size;
    }

    public int getCount_place() {
        return count_place;
    }

    public int getCount_pass() {
        return count_pass;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public String getTel() {
        return tel;
    }

    public String getImage_person() {
        return image_person;
    }

    public float getRating() {
        return rating;
    }

    public String getImage_car() {
        return image_car;
    }

    public String getCar_brand() {
        return car_brand;
    }

    public String getCar_model() {
        return car_model;
    }

    public boolean getReserve() {
        return reserve;
    }

    public ArrayList<PassengerItem> getPassengers() {
        return passengers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private OrderItem(Parcel parcel) {
        setId(parcel.readInt());
        Object[] object = parcel.readArray(getClass().getClassLoader());
        String[] places = Arrays.copyOf(object, object.length, String[].class);
        setPlaces(places);
        setLuggage_size(parcel.readString());
        setCount_place(parcel.readInt());
        setCount_pass(parcel.readInt());
        setDate(parcel.readString());
        setTime(parcel.readString());

        setName(parcel.readString());
        setTel(parcel.readString());
        setImage_person(parcel.readString());
        setRating(parcel.readFloat());

        setImage_car(parcel.readString());
        setCar_brand(parcel.readString());
        setCar_model(parcel.readString());
        setPassengers(parcel.readArrayList(getClass().getClassLoader()));

        setReserve(parcel.readByte() != 0);
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(getId());
        parcel.writeArray(getPlaces());
        parcel.writeString(getLuggage_size());
        parcel.writeInt(getCount_place());
        parcel.writeInt(getCount_pass());
        parcel.writeString(getDate());
        parcel.writeString(getTime());

        parcel.writeString(getName());
        parcel.writeString(getTel());
        parcel.writeString(getImage_person());
        parcel.writeFloat(getRating());

        parcel.writeString(getImage_car());
        parcel.writeString(getCar_brand());
        parcel.writeString(getCar_model());
        parcel.writeList(getPassengers());

        parcel.writeByte((byte) (getReserve() ? 1 : 0));
    }

    public static final Parcelable.Creator<OrderItem> CREATOR = new Parcelable.Creator<OrderItem>() {

        @Override
        public OrderItem createFromParcel(Parcel source) {
            return new OrderItem(source);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };

}
