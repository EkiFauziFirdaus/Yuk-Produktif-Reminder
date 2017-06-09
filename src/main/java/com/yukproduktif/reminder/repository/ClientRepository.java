package com.yukproduktif.reminder.repository;

import org.springframework.data.repository.CrudRepository;

import com.yukproduktif.reminder.model.Client;

public interface ClientRepository extends CrudRepository<Client, Integer> {

}
