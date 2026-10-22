package com.byrent.repository;

import com.byrent.model.Rental;
import java.util.HashMap;
import java.util.Map;

public class InMemoryRentalRepository implements RentalRepository {
    private final Map<String, Rental> storage = new HashMap<>();

    @Override
    public void save(Rental rental) {
        storage.put(rental.getId(), rental);
    }

    @Override
    public Rental findById(String id) {
        return storage.get(id);
    }
}