package com.proptiger.data.util;

import com.proptiger.data.model.Property;
import com.proptiger.data.model.enums.DomainObject;

/**
 * A converter utility that converts id of domian objects from proptiger DB to
 * cms DB and vice versa
 * 
 * @author Rajeev Pandey
 * @author mukand
 * 
 */
public class IdConverterForDatabase {

    public static Integer convertProjectIdFromCMSToProptiger(Property property) {
        Integer projectId = 0;
        if (property != null && property.getProjectId() > DomainObject.project.getStartId()) {
            projectId = property.getProjectId() - DomainObject.project.getStartId();
        }
        return projectId;
    }

    public static Integer convertPropertyIdFromCMSToProptiger(Integer propertyId) {
        Integer typeId = 0;
        if (propertyId != null && propertyId > DomainObject.property.getStartId()) {
            typeId = propertyId - DomainObject.property.getStartId();
        }
        return typeId;
    }

    public static int getProptigerDomainIdForDomainTypes(DomainObject domainObject, int id) {
        int startId = domainObject.getStartId();
        if (id > startId) {
            return id - startId;
        }
        return id;
    }

    public static int getCMSDomainIdForDomainTypes(DomainObject domainObject, int id) {
        int startId = domainObject.getStartId();
        if (id < startId) {
            return id + startId;
        }
        return id;
    }

}
