package com.byrent;

import com.byrent.model.Bicycle;
import com.byrent.model.Customer;
import com.byrent.model.Rental;
import com.byrent.repository.InMemoryRentalRepository;
import com.byrent.repository.RentalRepository;
import com.byrent.service.RentalService;

public class Main {
    public static void main(String[] args) {
        RentalRepository repository = new InMemoryRentalRepository();
        RentalService rentalService = new RentalService(repository);

        Customer customer = new Customer("C-001", "Леонова Полина", "+375299263712");
        Bicycle bicycle = new Bicycle("B-001", "Giant Escape", Bicycle.STATUS_AVAILABLE);

        Rental rental = rentalService.createRental("R-100", customer, bicycle);
        System.out.println("Успешно создана аренда: " + rental.getId() + " для " + rental.getCustomer().getName());
    }
}