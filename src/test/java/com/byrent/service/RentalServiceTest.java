package com.byrent.service;

import com.byrent.model.Bicycle;
import com.byrent.model.Customer;
import com.byrent.model.Rental;
import com.byrent.repository.InMemoryRentalRepository;
import com.byrent.repository.RentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class RentalServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RentalServiceTest.class);

    private RentalRepository rentalRepository;
    private RentalService rentalService;

    @BeforeEach
    void setUp() {
        rentalRepository = new InMemoryRentalRepository();
        rentalService = new RentalService(rentalRepository);
    }

    // успешный сценарий
    @Test
    void testSuccessfulRentalCreation() {
        Customer customer = new Customer("C-100", "Леонова Полина", "+375299263712");
        Bicycle bicycle = new Bicycle("B-001", "Giant Escape", Bicycle.STATUS_AVAILABLE);

        LOGGER.info("Test: Attempt to successfully rent a bicycle {}", bicycle.getId());

        Rental rental = rentalService.createRental("R1", customer, bicycle);

        assertNotNull(rental);
        assertEquals("R1", rental.getId());
        assertEquals(Bicycle.STATUS_RENTED, bicycle.getStatus());
        assertEquals(rental, rentalRepository.findById("R1"));

        LOGGER.info("Test: Rental successfully created");
    }

    // велосипед уже арендован
    @Test
    void testRentalThrowsExceptionWhenBicycleIsAlreadyRented() {
        Customer customer1 = new Customer("C-002", "Широков Константин", "+375291112233");
        Customer customer2 = new Customer("C-003", "Витковская Анастасия", "+375336667788");
        Bicycle bicycle = new Bicycle("B-002", "Trek New IPPRPO", Bicycle.STATUS_AVAILABLE);

        rentalService.createRental("R1", customer1, bicycle);

        LOGGER.warn("Test: Attempting to rent an already occupied bicycle {}", bicycle.getId());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            rentalService.createRental("R2", customer2, bicycle);
        });

        assertEquals("Велосипед уже арендован.", exception.getMessage());
    }

    // велосипед на обслуживании
    @Test
    void testRentalThrowsExceptionWhenBicycleIsInMaintenance() {
        Customer customer = new Customer("C-004", "Леонович Илья", "+375440011996");
        Bicycle bicycle = new Bicycle("B-004", "Update for Part4_4", Bicycle.STATUS_IN_MAINTENANCE);

        LOGGER.warn("Test: Attempt to rent a bicycle that is undergoing maintenance {}", bicycle.getId()); 

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            rentalService.createRental("R3", customer, bicycle);
        });

        assertEquals("Велосипед находится на обслуживании.", exception.getMessage());
    }

    @Test
    void testRentalContainsCorrectDetails() {
        Customer customer = new Customer("C-005", "Колыбелкин Дмитрий", "+375298765432");
        Bicycle bicycle = new Bicycle("B-005", "Individual", Bicycle.STATUS_AVAILABLE);

        Rental rental = rentalService.createRental("R4", customer, bicycle);

        assertAll("Проверка полей аренды",
            () -> assertEquals("C-005", rental.getCustomer().getId()),
            () -> assertEquals("Individual", rental.getBicycle().getModel()),
            () -> assertEquals(Bicycle.STATUS_RENTED, rental.getBicycle().getStatus())
        );
    }
}