package com.dev.booking.models;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class EmailStatus {
	private String value;
	private int count;
}
