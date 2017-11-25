package ru.shuttlecar.shuttlecar.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;


public class MarkerItem implements ClusterItem {

    private LatLng mPosition;
    private String mTitle;

    public MarkerItem(String title, double lat, double lng) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getTitle() {
        return mTitle;
    }

}