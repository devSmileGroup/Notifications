package com.dev.booking.models;

import lombok.Data;

@Data
public class EmailInfo {
	private EmailStatus status;
	private int sendingCount;
}
