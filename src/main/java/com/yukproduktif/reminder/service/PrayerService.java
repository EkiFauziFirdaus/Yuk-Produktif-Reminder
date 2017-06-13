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
	
	private static final String CRON_TIME = "0 0 5 * * *";
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
	
	private String getFardhPrayer(JSONObject json, String prayerName) throws JSONException {
		return json.getJSONObject("wajib").getString(prayerName);
	}
	
	private String getSunnahPrayer(JSONObject json, String prayerName) throws JSONException {
		return json.getJSONObject("sunnah").getString(prayerName);
	}
	
	private void saveData(JSONObject json) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		List<Prayer> prayers = new ArrayList<Prayer>();
		try {
			prayers.add(new Prayer(1, "tahajud", new Time(format.parse(getSunnahPrayer(json, "tahajud")).getTime()), "sunnah", false));
			prayers.add(new Prayer(2, "shubuh", new Time(format.parse(getFardhPrayer(json, "shubuh")).getTime()), "fardh", true));
			prayers.add(new Prayer(3, "dhuha", new Time(format.parse(getSunnahPrayer(json, "dhuha")).getTime()), "sunnah", true));
			prayers.add(new Prayer(4, "dzuhur", new Time(format.parse(getFardhPrayer(json, "dzuhur")).getTime()), "fardh", true));
			prayers.add(new Prayer(5, "ashar", new Time(format.parse(getFardhPrayer(json, "ashar")).getTime()), "fardh", true));
			prayers.add(new Prayer(6, "magrib", new Time(format.parse(getFardhPrayer(json, "magrib")).getTime()), "fardh", true));
			prayers.add(new Prayer(7, "isya", new Time(format.parse(getFardhPrayer(json, "isya")).getTime()), "fardh", true));
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
