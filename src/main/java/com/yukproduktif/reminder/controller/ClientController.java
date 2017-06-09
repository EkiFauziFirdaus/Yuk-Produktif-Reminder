package com.yukproduktif.reminder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.yukproduktif.reminder.model.Client;
import com.yukproduktif.reminder.repository.ClientRepository;

@Controller
public class ClientController {
	
	@Autowired
	ClientRepository clientRepo;
	
	@RequestMapping(value = "/sign-up", method = RequestMethod.GET)
	public String showSignUpForm(Model model) {
		model.addAttribute("client", new Client());
		return "sign-up";
	}
	
	@RequestMapping(value = "/save-client-data", method = RequestMethod.POST)
	public String saveClientData(@ModelAttribute("client")Client client) {
		if (clientRepo.exists(client.getId()) == false) {
			client.setId((int)clientRepo.count() + 1);
			client.setAccessToken("Key-"+clientRepo.count());
		}
		clientRepo.save(client);
		return "redirect:/";
	}

}
