package com.proptiger.data.model;

public enum DomainObject {
	project("project"),
	property("property"),
	builder("builder"),
	locality("locality"),
    city("city"),
    suburb("suburb"),
	bank("bank");

	String text;
	DomainObject(String x) {
		text = x;
	}
	
	public String getText() {
		return text;
	}
}