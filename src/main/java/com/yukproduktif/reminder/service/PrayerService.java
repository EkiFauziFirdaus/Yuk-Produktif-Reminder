package com.yukproduktif.reminder.service;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.springframework.stereotype.Component;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.yukproduktif.reminder.model.Prayer;
import com.yukproduktif.reminder.repository.PrayerRepository;

@Component
public class PrayerService {
	
	private static final String CRON_TIME = "0 0 0 * * *";
	private static final String LOCATION = "bandung";
	private static final String TRIGGER_TIME = "0 25 * * * *";
	private static final String URL = "https://adzanservice.herokuapp.com/";
	protected Logger logger = Logger.getLogger(PrayerService.class.getName());
	
	@Autowired
	PrayerRepository prayerRepo;
	
	private Calendar getCalendar() {
		TimeZone timeZone = TimeZone.getTimeZone("Asia/Jakarta");
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeZone(timeZone);
		
		return calendar;
	}
	
	private int getDay(Calendar calendar) {
		return calendar.get(Calendar.DAY_OF_MONTH);
	}
	
	private int getMonth(Calendar calendar) {
		return calendar.get(Calendar.MONTH) + 1;
	}
	
	private int getYear(Calendar calendar) {
		return calendar.get(Calendar.YEAR);
	}
	
	private String getPrayer(JSONObject json, String prayerName) throws JSONException {
		return json.getString(prayerName);
	}
	
	private void saveData(JSONObject json) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		List<Prayer> prayers = new ArrayList<Prayer>();
		try {
			prayers.add(new Prayer(1, "Shubuh",new Time(format.parse(getPrayer(json, "Shubuh")).getTime())));
			prayers.add(new Prayer(2, "Dzuhur",new Time(format.parse(getPrayer(json, "Dzuhur")).getTime())));
			prayers.add(new Prayer(3, "Ashar",new Time(format.parse(getPrayer(json, "Ashar")).getTime())));
			prayers.add(new Prayer(4, "Magrib",new Time(format.parse(getPrayer(json, "Magrib")).getTime())));
			prayers.add(new Prayer(5, "Isya",new Time(format.parse(getPrayer(json, "Isya")).getTime())));
			prayerRepo.save(prayers);
			logger.info("Data saved.");
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Scheduled(cron = TRIGGER_TIME, zone = "Asia/Jakarta")
	private void triggerPrayerAPI() {
		Unirest.get(URL);
	}
	
	@Scheduled(cron = CRON_TIME, zone = "Asia/Jakarta")
	private void getPrayerData() {
		try {
			Calendar systemDate = getCalendar();
			int date = getDay(systemDate);
			int month = getMonth(systemDate);
			int year = getYear(systemDate);
			String prayerServiceUrl = URL + 
					"get_adzan" +
					"/" + date + 
					"/" + month + 
					"/" + year + 
					"/" + LOCATION;
			JSONObject json = Unirest.get(prayerServiceUrl).asJson().getBody().getObject();
			saveData(json);
		} catch (UnirestException e) {
			e.printStackTrace();
		}
	}
}
