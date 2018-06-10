package com.dev.booking.models;

import lombok.Data;

@Data
public class Status {
	private UIStatusValue uiStatus;
	private EmailStatus emailStatus;
}
