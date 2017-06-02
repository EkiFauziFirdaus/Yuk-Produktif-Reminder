package com.yukproduktif.reminder.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.yukproduktif.reminder.model.Client;
import com.yukproduktif.reminder.model.Prayer;
import com.yukproduktif.reminder.repository.ClientRepository;
import com.yukproduktif.reminder.repository.PrayerRepository;

@Controller
public class CronController {
	
	protected Logger logger = Logger.getLogger(CronController.class.getName());
	
	@Autowired
	ClientRepository clientRepo;
	
	@Autowired
	PrayerRepository prayerRepo;
	
	private static final String CRON_TIME = "0 */1 * * * *";
	private static final String PREFIX_URL = "http://";
	private Boolean getCronTimeFromDB = false;
	private int cronHour;
	private int cronMinutes;
	
	private Calendar getCurrentTime() {
		TimeZone timeZone = TimeZone.getTimeZone("Asia/Jakarta");
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeZone(timeZone);
		
		return calendar;
	}
	
	private int getCurrentHours() {
		return getCurrentTime().get(Calendar.HOUR_OF_DAY);
	}
	
	private int getCurrentMinutes() {
		return getCurrentTime().get(Calendar.MINUTE);
	}
	
	private int getCronHours() {
		return prayerRepo.findByStatus(false).getTime().getHours();
	}
	
	private int getCronMinutes() {
		return prayerRepo.findByStatus(false).getTime().getMinutes();
	}
	
	private JSONObject generatebody(Client client, Prayer prayer) throws JSONException {
		JSONObject prayerData = new JSONObject();
		prayerData.put("name", prayer.getName());
		prayerData.put("time", prayer.getTime());
		JSONObject body = new JSONObject();
		body.put("token", client.getAccessToken());
		body.put("reminder", prayerData);
		
		return body;
	}
	
	private void getCrontime() {
		if (getCronTimeFromDB == false) {
			cronHour = getCronHours();
			cronMinutes = getCronMinutes();
			getCronTimeFromDB = true;
		}
		if (getCurrentHours() == 0 && getCurrentMinutes() == 0) {
			getCronTimeFromDB = false;
		}
	}
	
	private void setNextReminder(Prayer currentPrayer) {
		currentPrayer.setStatus(true);
		int id = (currentPrayer.getId() % (int)prayerRepo.count()) + 1;
		Prayer nextPrayer = prayerRepo.findOne(id);
		nextPrayer.setStatus(false);
		List<Prayer> prayers = new ArrayList<Prayer>();
		prayers.add(currentPrayer);
		prayers.add(nextPrayer);
		prayerRepo.save(prayers);
		getCronTimeFromDB = false;
	}
	
	@Scheduled(cron = CRON_TIME, zone = "Asia/Jakarta")
	private void cron() {
		int currentHour = getCurrentHours();
		int currentMinutes = getCurrentMinutes();
		getCrontime();
		logger.info("Current Time= "+currentHour+":"+currentMinutes);
		logger.info("Cron Time= "+cronHour+":"+cronMinutes);
		if ((currentHour == cronHour) && (currentMinutes == cronMinutes)) {
			Prayer prayer = prayerRepo.findByStatus(false);
			for (int i = 1; i <= clientRepo.count(); i++) {
				Client client = clientRepo.findOne(i);
				try {
					HttpResponse<JsonNode> response = Unirest.post(PREFIX_URL + client.getCallback())
							.body(generatebody(client, prayer))
							.asJson();
					JSONObject json = response.getBody().getObject();
					logger.info(json.toString());
				} catch (UnirestException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			setNextReminder(prayer);
		}
		/*DEBUG PURPOSE*/
		/*
		if ((currentHour == cronHour) && (currentMinutes == cronMinutes)) {
			Prayer prayer = prayerRepo.findByStatus(false);
			for (int i = 1; i <= clientRepo.count(); i++) {
				Client client = clientRepo.findOne(i);
				try {
					logger.info(generatebody(client, prayer).toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			setNextReminder(prayer);
		}
		*/
	}
}
