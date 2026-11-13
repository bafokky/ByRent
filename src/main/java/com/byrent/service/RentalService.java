package com.byrent.service;

import com.byrent.model.Bicycle;
import com.byrent.model.Customer;
import com.byrent.model.Rental;
import com.byrent.repository.RentalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RentalService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RentalService.class);

    private final RentalRepository rentalRepository;

    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public Rental createRental(String rentalId, Customer customer, Bicycle bicycle) {

        LOGGER.info("Request to create a rental. ID: {}, Client: {}", rentalId, customer.getName());

        if (Bicycle.STATUS_RENTED.equals(bicycle.getStatus())) {
            LOGGER.warn("Declined. Bicycle {} is already rented.", bicycle.getId());
            throw new IllegalStateException("Велосипед уже арендован.");
        }
        if (Bicycle.STATUS_IN_MAINTENANCE.equals(bicycle.getStatus())) {
            LOGGER.warn("Declined. Bicycle {} is undergoing maintenance.", bicycle.getId());
            throw new IllegalStateException("Велосипед находится на обслуживании.");
        }

        bicycle.setStatus(Bicycle.STATUS_RENTED);

        Rental rental = new Rental(rentalId, customer, bicycle);
        try {
            rentalRepository.save(rental);
            LOGGER.info("Rental successfully created. ID: {}", rentalId);
        } catch (Exception e) {
            LOGGER.error("Error while saving rental with ID: {}", rentalId, e);
            throw e;
        }

        return rental;
    }
}
