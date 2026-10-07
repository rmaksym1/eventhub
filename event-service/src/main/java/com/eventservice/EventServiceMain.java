package com.eventservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"com.eventservice",
		"com.eventhub.common"
})
public class EventServiceMain {

    public static void main(String[] args) {
		SpringApplication.run(EventServiceMain.class, args);
	}

}
