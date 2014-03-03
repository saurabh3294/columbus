package com.proptiger.data.service.b2b;

import java.util.Date;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.joda.JodaTimeContext;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.b2b.Catchment;
import com.proptiger.data.model.b2b.STATUS;
import com.proptiger.data.repo.b2b.CatchmentDao;

@Service
public class CatchmentService {
    @Autowired
    private CatchmentDao catchmentDao;
    private Logger       logger = LoggerFactory.getLogger(CatchmentService.class);
 
    public Catchment test(){
//        catchmentDao.findOne(id);
        
        Catchment catchment = new Catchment();
        
        catchment.setCatchment("");
//        catchment.setCreatedAt(new DateTime());
//        catchment.setUpdatedAt(new DateTime());
        catchment.setName("testing");
        catchment.setStatus(STATUS.Active);
        catchment.setUserId(1);
        return catchmentDao.save(catchment);
    }
}