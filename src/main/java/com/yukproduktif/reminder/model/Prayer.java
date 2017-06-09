package com.yukproduktif.reminder.model;

import java.sql.Time;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_prayer")
public class Prayer {
	
	private int id;
	private String name;
	private Time time;
	private Boolean status;
	private String type;
	
	@Id
	@Column(name = "prayer_id", unique = true, nullable =  false)
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name = "prayer_name", unique = true, nullable = false)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "prayer_time", nullable = false)
	public Time getTime() {
		return time;
	}
	
	public void setTime(Time time) {
		this.time = time;
	}
	
	@Column(name = "prayer_type", nullable = false)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Column(name = "reminder_status", nullable = false)
	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Prayer() {}

	public Prayer(int id, String name, Time time, String type, Boolean status) {
		super();
		this.id = id;
		this.name = name;
		this.time = time;
		this.type = type;
		this.status = status;
	}
	
}
