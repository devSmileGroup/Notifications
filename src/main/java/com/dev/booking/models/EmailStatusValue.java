package com.dev.booking.models;

public enum EmailStatusValue {
	NEW("new"),
	IN_PROCESS("in process"),
	PROCESSED("processed"),
	FAILED("failed");
	
	private final String name;
	
	private EmailStatusValue(String s) {
		name = s;
	}
	
	public String toString() {
		return this.name;
	}
}
