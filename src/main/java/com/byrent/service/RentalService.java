package com.byrent.service;

import com.byrent.model.Bicycle;
import com.byrent.model.Customer;
import com.byrent.model.Rental;
import com.byrent.repository.RentalRepository;
import io.micrometer.core.instrument.Counter; 
import io.micrometer.core.instrument.MeterRegistry; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RentalService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RentalService.class);

    private final RentalRepository rentalRepository;

    // счётчики
    private final Counter totalOperationsCounter;
    private final Counter successOperationsCounter;
    private final Counter errorOperationsCounter;

    public RentalService(RentalRepository rentalRepository, MeterRegistry registry) {
        this.rentalRepository = rentalRepository;
        this.totalOperationsCounter = registry.counter("application_operations_total");
        this.successOperationsCounter = registry.counter("application_operations_success_total");
        this.errorOperationsCounter = registry.counter("application_operations_error_total");
    }

    public Rental createRental(String rentalId, Customer customer, Bicycle bicycle) {
        totalOperationsCounter.increment();

        LOGGER.info("Request to create a rental. ID: {}, Client: {}", rentalId, customer.getName());

        try {
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
            rentalRepository.save(rental);
            LOGGER.info("Rental successfully created. ID: {}", rentalId);
            successOperationsCounter.increment();
            
            return rental;

        } catch (IllegalStateException e) {
            errorOperationsCounter.increment();
            throw e; 
        } catch (Exception e) {
            LOGGER.error("Error while saving rental with ID: {}", rentalId, e);
            errorOperationsCounter.increment();
            throw e; 
        }
    }
}
