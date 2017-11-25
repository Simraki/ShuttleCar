package ru.shuttlecar.shuttlecar.network;

import ru.shuttlecar.shuttlecar.network.map.Route;

public class ServerResponse {

    private String result;
    private String message;
    private User user;
    private Order[] orders;

    private Route[] routes;
    private String status;

    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public Order[] getOrders() {
        return orders;
    }

    public Route[] getRoutes() {
        return routes;
    }

    public String getStatus() {
        return status;
    }
}
