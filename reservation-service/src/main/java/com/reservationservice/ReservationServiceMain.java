package com.reservationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
		"com.reservationservice",
		"com.eventhub.common"
})
@EnableScheduling
public class ReservationServiceMain {

    public static void main(String[] args) {
		SpringApplication.run(ReservationServiceMain.class, args);
	}

}
