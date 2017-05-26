package com.yukproduktif.reminder.service;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.yukproduktif.reminder.model.Prayer;
import com.yukproduktif.reminder.repository.PrayerRepository;

@Component
public class PrayerService {
	
	String data = "{\"Shubuh\" : \"05:00\", \"Dzuhur\" : \"12:00\", \"Ashar\" : \"15:00\", \"Magrib\" : \"06:00\", \"Isya\" : \"07:00\" }";
	protected Logger logger = Logger.getLogger(PrayerService.class.getName());
	
	@Autowired
	PrayerRepository prayerRepo;
	
	void saveData(JSONObject json) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		List<Prayer> prayers = new ArrayList<Prayer>();
		try {
			prayers.add(new Prayer(1, "Shubuh",new Time(format.parse(json.getString("Shubuh")).getTime())));
			prayers.add(new Prayer(2, "Dzuhur",new Time(format.parse(json.getString("Dzuhur")).getTime())));
			prayers.add(new Prayer(3, "Ashar",new Time(format.parse(json.getString("Ashar")).getTime())));
			prayers.add(new Prayer(4, "Magrib",new Time(format.parse(json.getString("Magrib")).getTime())));
			prayers.add(new Prayer(5, "Isya",new Time(format.parse(json.getString("Isya")).getTime())));
			prayerRepo.save(prayers);
			logger.info("Data saved.");
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Scheduled(cron = "*/1 * * * * *")
	void getPrayerData() {
		try {
			JSONObject json = new JSONObject(data);
			saveData(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
