package ru.shuttlecar.shuttlecar.network;

public class Order {
    private int id;
    private int[] ids;
    private int[] ids_del;
    private String[] places;
    private String pdis;
    private String pdel;
    private String time;
    private String date;
    private int price;
    private int count_place;
    private int count_pass;
    private String lugg;
    private User driver;
    private Passenger[] passengers;


    public int getId() {
        return id;
    }

    public String[] getPlaces() {
        return places;
    }

    public String getPdis() {
        return pdis;
    }

    public String getPdel() {
        return pdel;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public int getPrice() {
        return price;
    }

    public int getCount_Place() {
        return count_place;
    }

    public int getCount_pass() {
        return count_pass;
    }

    public String getLuggage() {
        return lugg;
    }

    public User getDriver() {
        return driver;
    }

    public Passenger[] getPassengers() {
        return passengers;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIds(int[] ids) {
        this.ids = ids;
    }

    public void setIds_del(int[] ids_del) {
        this.ids_del = ids_del;
    }

    public void setPlaces(String places[]) {
        this.places = places;
    }

    public void setPdis(String pdis) {
        this.pdis = pdis;
    }

    public void setPdel(String pdel) {
        this.pdel = pdel;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setCount_Place(int count_place) {
        this.count_place = count_place;
    }

    public void setCount_pass(int count_pass) {
        this.count_pass = count_pass;
    }

    public void setLugg(String lugg) {
        this.lugg = lugg;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }
}
