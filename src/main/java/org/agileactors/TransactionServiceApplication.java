package org.agileactors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for starting the Transaction Service application.
 */
@SpringBootApplication
public class TransactionServiceApplication {

    /**
     * The entry point of the application.
     *
     * @param args The command line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(TransactionServiceApplication.class, args);
    }
}