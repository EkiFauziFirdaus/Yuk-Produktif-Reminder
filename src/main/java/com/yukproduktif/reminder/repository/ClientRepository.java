package com.yukproduktif.reminder.repository;

import org.springframework.data.repository.CrudRepository;

import ch.qos.logback.core.net.server.Client;

public interface ClientRepository extends CrudRepository<Client, Integer> {

}
