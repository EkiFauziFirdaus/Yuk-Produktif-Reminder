package com.yukproduktif.reminder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class YukProduktifReminderApplication {

	public static void main(String[] args) {
		SpringApplication.run(YukProduktifReminderApplication.class, args);
	}
}
