package com.yukproduktif.reminder.controller;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.yukproduktif.reminder.repository.ClientRepository;

@Controller
public class CronController {
	
protected Logger logger = Logger.getLogger(CronController.class.getName());
	
	@Autowired
	ClientRepository clientRepo;
	
	@Scheduled(cron = "*/1 * * * * *")
	public void cronToClient() {
		for (int i = 1; i <= clientRepo.count(); i++) {
			logger.info("Token: "+clientRepo.findOne(i).getAccessToken());
		}
	}
}
