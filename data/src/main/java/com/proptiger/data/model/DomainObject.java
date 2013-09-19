package com.proptiger.data.model;

public enum DomainObject {
	PROJECT("project"),
	PROPERTY("property"),
	BUILDER("builder"),
	LOCALITY("locality"),
	BANK("bank");
	
	String text;
	DomainObject(String x) {
		text = x;
	}
	
	public String getText() {
		return text;
	}
}