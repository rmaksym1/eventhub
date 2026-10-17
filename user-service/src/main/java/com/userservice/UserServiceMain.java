package com.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"com.userservice",
		"com.eventhub.common"
})
public class UserServiceMain {

    public static void main(String[] args) {
		SpringApplication.run(UserServiceMain.class, args);
	}

}
