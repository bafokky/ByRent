package com.byrent.service;

import com.byrent.model.Bicycle;
import com.byrent.model.Customer;
import com.byrent.model.Rental;
import com.byrent.repository.RentalRepository;

public class RentalService {
    private final RentalRepository rentalRepository;

    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public Rental createRental(String rentalId, Customer customer, Bicycle bicycle) {
        if (Bicycle.STATUS_RENTED.equals(bicycle.getStatus())) {
            throw new IllegalStateException("Велосипед уже арендован.");
        }
        if (Bicycle.STATUS_IN_MAINTENANCE.equals(bicycle.getStatus())) {
            throw new IllegalStateException("Велосипед находится на обслуживании.");
        }

        bicycle.setStatus(Bicycle.STATUS_RENTED);

        Rental rental = new Rental(rentalId, customer, bicycle);
        rentalRepository.save(rental);

        return rental;
    }
}