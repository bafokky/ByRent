package com.byrent.model;

public class Bicycle {
    public static final String STATUS_AVAILABLE = "AVAILABLE";
    public static final String STATUS_RENTED = "RENTED";
    public static final String STATUS_IN_MAINTENANCE = "IN_MAINTENANCE";

    private final String id;
    private final String model;
    private String status;

    public Bicycle(String id, String model, String status) {
        this.id = id;
        this.model = model;
        this.status = status;
    }

    public String getId() { return id; }
    public String getModel() { return model; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
