package ru.shuttlecar.shuttlecar.models;


import android.os.Parcel;
import android.os.Parcelable;

public class PassengerItem implements Parcelable {

    private int id;
    private String name;
    private String image_person;

    public PassengerItem(int id, String name, String image_person) {
        this.id = id;
        this.name = name;
        this.image_person = image_person;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage_person(String image_person) {
        this.image_person = image_person;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage_person() {
        return image_person;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private PassengerItem(Parcel parcel) {
        setId(parcel.readInt());
        setName(parcel.readString());
        setImage_person(parcel.readString());
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(getId());
        parcel.writeString(getName());
        parcel.writeString(getImage_person());

    }

    public static final Parcelable.Creator<PassengerItem> CREATOR = new Parcelable.Creator<PassengerItem>() {

        @Override
        public PassengerItem createFromParcel(Parcel source) {
            return new PassengerItem(source);
        }

        @Override
        public PassengerItem[] newArray(int size) {
            return new PassengerItem[size];
        }
    };
}
