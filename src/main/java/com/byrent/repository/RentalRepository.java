package com.byrent.repository;

import com.byrent.model.Rental;

public interface RentalRepository {
    void save(Rental rental);
    Rental findById(String id);
}