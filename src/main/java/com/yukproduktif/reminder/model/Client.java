package com.yukproduktif.reminder.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_client")
public class Client {
	
	private int id;
	private String callback;
	private String accessToken;
	
	@Id
	@Column(name = "client_id", unique = true, nullable = false)
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name = "client_callback", nullable = false)
	public String getCallback() {
		return callback;
	}
	
	public void setCallback(String callback) {
		this.callback = callback;
	}
	
	@Column(name = "client_access_token", nullable = false)
	public String getAccessToken() {
		return accessToken;
	}
	
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public Client() {}

	public Client(int id, String callback, String accessToken) {
		super();
		this.id = id;
		this.callback = callback;
		this.accessToken = accessToken;
	}
	
}
