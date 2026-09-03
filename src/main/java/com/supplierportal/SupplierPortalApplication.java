package com.supplierportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Composition root of the Supplier Portal application.
 *
 * <p>This is the only class in the codebase allowed to wire together
 * classes across every architectural layer (domain, application,
 * infrastructure) - Spring's component scan does that wiring starting
 * from here. See Section 4 of the Software Architecture Document for
 * the Clean Architecture layering this project follows.</p>
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class SupplierPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplierPortalApplication.class, args);
    }
}
