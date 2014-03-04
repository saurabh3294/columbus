package com.proptiger.data.service.b2b;

import java.util.Date;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.model.b2b.Catchment;
import com.proptiger.data.repo.b2b.CatchmentDao;
import com.proptiger.exception.ResourceAlreadyExistException;

@Service
public class CatchmentService {
    @Autowired
    private CatchmentDao catchmentDao;

    @Transactional
    public Catchment createCatchment(Catchment catchment, UserInfo userInfo) {
        catchment.setCreatedAt(new Date());
        catchment.setUpdatedAt(new Date());
        catchment.setUserId(userInfo.getUserIdentifier());
        if (true) {
            try {
                catchmentDao.findByName(catchment.getName());
                return catchmentDao.save(catchment);
            }
            catch (PersistenceException e) {
                if (e.getCause() != null && e.getCause().getCause() instanceof MySQLIntegrityConstraintViolationException) {
                    throw new ResourceAlreadyExistException(
                            "Catchment name " + catchment.getName() + " already taken",
                            ResponseCodes.CATCHMENTNAME_TAKEN);
                }
                throw new RuntimeException("Error");
            }
        }
        throw new IllegalArgumentException("Invalid Input Provided");
    }
}