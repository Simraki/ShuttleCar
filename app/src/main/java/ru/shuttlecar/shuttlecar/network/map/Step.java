package ru.shuttlecar.shuttlecar.network.map;

class Step {
    private PolyLine polyline;
    private String travel_mode;
    private Transit_detail transit_details;

    public String getTravel_mode() {
        return travel_mode;
    }

    public PolyLine getPolyline() {
        return polyline;
    }

    public Transit_detail getTransit_details() {
        return transit_details;
    }
}
