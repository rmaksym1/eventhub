package com.reservationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"com.reservationservice",
		"com.eventhub.common"
})
public class ReservationServiceMain {

    public static void main(String[] args) {
		SpringApplication.run(ReservationServiceMain.class, args);
	}

}
