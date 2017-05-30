package com.yukproduktif.reminder.controller;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

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
	private int prayerId = 1;
	
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
	
	private int getCronHours(int id) {
		return prayerRepo.findOne(id).getTime().getHours();
	}
	
	private int getCronMinutes(int id) {
		return prayerRepo.findOne(id).getTime().getMinutes();
	}
	
	private int setPrayerId(int id) {
		return (id % (int)prayerRepo.count()) + 1;
	}
	
	private JSONObject generatebody(Client client, Prayer prayer) throws JSONException {
		JSONObject prayerData = new JSONObject();
		prayerData.put("Name", prayer.getName());
		prayerData.put("Time", prayer.getTime());
		JSONObject body = new JSONObject();
		body.put("Token", client.getAccessToken());
		body.put("Reminder", prayerData);
		
		return body;
	}
	
	@Scheduled(cron = CRON_TIME, zone = "Asia/Jakarta")
	private void cron() {
		int currentHour = getCurrentHours();
		int currentMinutes = getCurrentMinutes();
		int cronHour = getCronHours(prayerId);
		int cronMinutes = getCronMinutes(prayerId);
		if ((currentHour == cronHour) && (currentMinutes == cronMinutes)) {
			Prayer prayer = prayerRepo.findOne(prayerId);
			for (int i = 1; i <= clientRepo.count(); i++) {
				Client client = clientRepo.findOne(i);
				try {
					JSONObject json = Unirest.post(PREFIX_URL + client.getCallback())
							.body(generatebody(client, prayer))
							.asJson()
							.getBody()
							.getObject();
					logger.info(json.toString());
				} catch (UnirestException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			prayerId = setPrayerId(prayerId);
		}
	}
}
