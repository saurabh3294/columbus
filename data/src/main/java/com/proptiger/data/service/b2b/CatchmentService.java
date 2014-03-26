package com.proptiger.data.service.b2b;

import java.util.List;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.model.b2b.Catchment;
import com.proptiger.data.model.b2b.CatchmentProject;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.b2b.CatchmentDao;
import com.proptiger.exception.ResourceAlreadyExistException;

@Service
public class CatchmentService {
    @Autowired
    private CatchmentDao catchmentDao;

    @Transactional
    public Catchment createCatchment(Catchment catchment, UserInfo userInfo) {
        catchment.setUserId(userInfo.getUserIdentifier());
        if (true) {
            try {
                for (CatchmentProject catchmentProject : catchment.getCatchmentProjects()) {
                    catchmentProject.setCatchment(catchment);
                }
                return catchmentDao.save(catchment);
            }
            catch (PersistenceException e) {
                if (e.getCause() != null && e.getCause().getCause() instanceof MySQLIntegrityConstraintViolationException) {
                    e.printStackTrace();
                    throw new ResourceAlreadyExistException(
                            "Catchment name " + catchment.getName() + " already taken",
                            ResponseCodes.CATCHMENTNAME_TAKEN);
                }
                throw new RuntimeException("Error");
            }
        }
        throw new IllegalArgumentException("Invalid Input Provided");
    }

    @Transactional
    public Catchment updateCatchment(Catchment catchment, UserInfo userInfo) {
        catchment.setUserId(userInfo.getUserIdentifier());
        Catchment savedCatchment = catchmentDao.findOne(catchment.getId());
//        if(savedCatchment.getUserId().equals(catchment.getUserId())){
            savedCatchment = catchment;
            for (CatchmentProject catchmentProject : savedCatchment.getCatchmentProjects()) {
                catchmentProject.setCatchment(savedCatchment);
            }
            savedCatchment.getCatchmentProjects().clear();
            catchmentDao.save(savedCatchment);
//        }
        return savedCatchment;
    }

    public List<Catchment> getCatchment(FIQLSelector fiqlSelector) {
        return catchmentDao.getFilteredCatchments(fiqlSelector);
    }
}