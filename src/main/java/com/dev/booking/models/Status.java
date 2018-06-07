package com.dev.booking.models;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import lombok.Data;


@Data
@Component
public class Status {
	private String uiStatus;
	
	@Inject
	private EmailStatus emailStatus;
}
