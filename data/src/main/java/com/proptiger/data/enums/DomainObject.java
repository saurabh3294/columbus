package com.proptiger.data.enums;

import java.util.HashMap;
import java.util.Map;

import com.proptiger.exception.ProAPIException;

public enum DomainObject {
    project("project", 500000, 1), property("property", 5000000, 2), builder("builder", 100000, 3), locality(
            "locality", 50000, 4), city("city", 0, 6), suburb("suburb", 10000, 7), bank("bank", 0, 5), brokerCompany(
            "brokerCompany", 0, 8), sellerCompany("sellerCompany", 0, 9), landmark("landmark", 0, 10), company(
            "company", 0, 11), leadtask("leadtask", 0, 12);

    private static final Map<Integer, DomainObject> domainObjectById = new HashMap<>();

    private final String                            text;
    private final int                               startId;
    private final int                               objectTypeId;

    static {
        for (DomainObject object : DomainObject.values()) {
            if (domainObjectById.put(object.getObjectTypeId(), object) != null) {
                throw new ProAPIException();
            }
        }
    }

    DomainObject(String x, int startId, int objectTypeId) {
        this.text = x;
        this.startId = startId;
        this.objectTypeId = objectTypeId;
    }

    public static DomainObject getDomainInstance(Long id) {

        if (id < suburb.getStartId()) {
            return city;
        }
        else if (id < locality.getStartId()) {
            return suburb;
        }
        else if (id < builder.getStartId()) {
            return locality;
        }
        else if (id < project.getStartId()) {
            return builder;
        }
        else if (id < property.getStartId()) {
            return project;
        }
        else {
            return property;
        }

    }

    public String getText() {
        return text;
    }

    public int getStartId() {
        return startId;
    }

    public int getObjectTypeId() {
        return objectTypeId;
    }

    public static DomainObject getFromObjectTypeId(int objectTypeId) {
        return domainObjectById.get(objectTypeId);
    }
}