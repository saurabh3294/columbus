package com.proptiger.data.repo.b2b;

import org.springframework.beans.factory.annotation.Autowired;

public class CatchmentDaoImpl {
    @Autowired
    CatchmentDao catchmentDao;
    public Integer countByName(String name){
        return catchmentDao.findByName(name).size();
    }
}
