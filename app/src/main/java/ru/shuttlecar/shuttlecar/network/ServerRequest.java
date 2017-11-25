package ru.shuttlecar.shuttlecar.network;


public class ServerRequest {

    private String operation;
    private Boolean type;
    private User user;
    private Order order;
    private Message message;

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
