package com.yukproduktif.reminder.repository;

import org.springframework.data.repository.CrudRepository;

import com.yukproduktif.reminder.model.Prayer;

public interface PrayerRepository extends CrudRepository<Prayer, Integer> {
	
	Prayer findByStatus(Boolean status);

}
