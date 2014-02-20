package com.proptiger.data.model.enums;

public enum DomainObject {
    project("project", 500000), property("property", 5000000), builder("builder", 100000), locality("locality", 50000), city(
            "city", 0), suburb("suburb", 10000), bank("bank", 0), brokerCompany("brokerCompany", 0), sellerCompany(
            "sellerCompany", 0);

    String text;
    int    startId;

    DomainObject(String x, int startId) {
        this.text = x;
        this.startId = startId;
    }

    public String getText() {
        return text;
    }

    public int getStartId() {
        return startId;
    }
}