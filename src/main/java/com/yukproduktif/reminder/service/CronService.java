package com.yukproduktif.reminder.service;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.yukproduktif.reminder.repository.ClientRepository;

public class CronService {
	
	protected Logger logger = Logger.getLogger(CronService.class.getName());
	
	@Autowired
	ClientRepository clientRepo;
	
	@Scheduled(cron = "*/1 * * * * *")
	public void cronToClient() {
		for (int i = 0; i < clientRepo.count(); i++) {
			logger.info("Token: "+clientRepo.findOne(i).getAccessToken());
		}
	}

}
