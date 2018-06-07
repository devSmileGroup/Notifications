package com.dev.booking.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Data;


@Data
@Component
public class Status {
	private String uiStatus;
	
	@Autowired
	private EmailStatus emailStatus;
}
