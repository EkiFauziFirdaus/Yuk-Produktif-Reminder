package com.yukproduktif.reminder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.yukproduktif.reminder.repository.ClientRepository;

public class CronService {
	
	@Autowired
	ClientRepository clientRepo;
	
	@Scheduled(cron = "*/1 * * * * *")
	public void cronToClient() {
		for (int i = 0; i < clientRepo.count(); i++) {
			System.out.println(clientRepo.findOne(i).getAccessToken());
		}
	}

}
