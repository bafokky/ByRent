package com.byrent.model;

import java.time.LocalDateTime;

public class Rental {
    private final String id;
    private final Customer customer;
    private final Bicycle bicycle;
    private final LocalDateTime startTime;

    public Rental(String id, Customer customer, Bicycle bicycle) {
        this.id = id;
        this.customer = customer;
        this.bicycle = bicycle;
        this.startTime = LocalDateTime.now();
    }

    public String getId() { return id; }
    public Customer getCustomer() { return customer; }
    public Bicycle getBicycle() { return bicycle; }
    public LocalDateTime getStartTime() { return startTime; }
}