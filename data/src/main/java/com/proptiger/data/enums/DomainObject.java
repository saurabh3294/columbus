package com.proptiger.data.enums;

public enum DomainObject {
    project("project", 500000), property("property", 5000000), builder("builder", 100000), locality("locality", 50000), city(
            "city", 0), suburb("suburb", 10000), bank("bank", 0), brokerCompany("brokerCompany", 0), sellerCompany(
            "sellerCompany", 0), landmark("landmark", 0);

    String text;
    int    startId;
    
    public static DomainObject getDomainInstance(Long id){
        
        if(id < suburb.getStartId()){
            return city;
        }
        else if(id < locality.getStartId()){
            return suburb;
        }
        else if(id < builder.getStartId()){
            return locality;
        }
        else if(id < project.getStartId()){
            return builder;
        }
        else if(id < property.getStartId()){
            return project;
        }
        else{ 
            return property;
        }
        
    }
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