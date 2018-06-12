package com.dev.booking.models;

public enum UIStatusValue {
	NEW("new"),
	READ("read");
	
	private final String name;
	
	private UIStatusValue(String s) {
		name = s;
	}
	
	public String toString() {
		return this.name;
	}
}
