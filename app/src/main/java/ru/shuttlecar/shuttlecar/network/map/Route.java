package ru.shuttlecar.shuttlecar.network.map;

public class Route {
    private Leg[] legs;
    private PolyLine overview_polyline;

    public Leg[] getLegs() {
        return legs;
    }

    public PolyLine getPolyline() {
        return overview_polyline;
    }
}
