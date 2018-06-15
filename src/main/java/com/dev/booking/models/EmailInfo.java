package com.dev.booking.models;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class EmailInfo {
	@Field("status")
	private EmailStatus emailStatus;
	@Field("sending_count")
	private int sendingCount;
}