package com.proptiger.data.repo;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.core.model.proptiger.ObjectType;

public class ObjectTypeDaoImpl {
    @Autowired
    private ObjectTypeDao objectTypeDao;

    public Integer getObjectTypeIdByType(String type) {
        ObjectType objectType = objectTypeDao.findByType(type);
        if (objectType != null) {
            return objectType.getId();
        }
        else {
            return null;
        }
    }
}
