package com.dev.booking.models;

import lombok.Data;

@Data
public class User {
	private String email;
	
	public User(String email) {
		this.email = email;
	}
}
