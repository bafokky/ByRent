package com.byrent;

import com.byrent.model.Bicycle;
import com.byrent.model.Customer;
import com.byrent.model.Rental;
import com.byrent.repository.InMemoryRentalRepository;
import com.byrent.repository.RentalRepository;
import com.byrent.service.RentalService;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws IOException{
        RentalRepository repository = new InMemoryRentalRepository();
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        RentalService rentalService = new RentalService(repository, registry);

        Customer customer = new Customer("C-001", "Леонова Полина", "+375299263712");
        Bicycle bicycle = new Bicycle("B-001", "Giant Escape", Bicycle.STATUS_AVAILABLE);

        Rental rental = rentalService.createRental("R-100", customer, bicycle);
        System.out.println("Успешно создана аренда: " + rental.getId() + " для " + rental.getCustomer().getName());

        Customer customer2 = new Customer("C-002", "Иванов Иван", "+375291112233");
        Bicycle bicycle2 = new Bicycle("B-002", "Trek Marlin", Bicycle.STATUS_AVAILABLE);
        rentalService.createRental("R-101", customer2, bicycle2);
        System.out.println("Успешно создана аренда R-101");

        Customer customer3 = new Customer("C-003", "Сидоров Алексей", "+375293334455");
        Bicycle bicycle3 = new Bicycle("B-003", "Merida Big Nine", Bicycle.STATUS_AVAILABLE);
        rentalService.createRental("R-102", customer3, bicycle3);
        System.out.println("Успешно создана аренда R-102");

        // ошибки
        try {
            System.out.println("Попытка арендовать уже занятый велосипед B-001...");
            rentalService.createRental("R-103", customer2, bicycle);
        } catch (IllegalStateException e) {
            System.out.println("Ожидаемая ошибка 1: " + e.getMessage());
        }

        Bicycle brokenBicycle = new Bicycle("B-004", "Stels Navigator", Bicycle.STATUS_IN_MAINTENANCE);
        try {
            System.out.println("Попытка арендовать велосипед на обслуживании B-004...");
            rentalService.createRental("R-104", customer3, brokenBicycle);
        } catch (IllegalStateException e) {
            System.out.println("Ожидаемая ошибка 2: " + e.getMessage());
        }

        
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/metrics", exchange -> {
            String response = registry.scrape(); 
            
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; version=0.0.4; charset=utf-8");
            exchange.sendResponseHeaders(200, responseBytes.length);
            
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        });
        
        //comm
        server.start(); 
        
        System.out.println("\nServer running");
        System.out.println("Метрики доступны по адресу: http://localhost:8080/metrics");
    }
}
