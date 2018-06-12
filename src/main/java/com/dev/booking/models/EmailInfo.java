package com.dev.booking.models;

import lombok.Data;

@Data
public class EmailStatus {
	private EmailStatusValue value;
	private int failedCount;
}
