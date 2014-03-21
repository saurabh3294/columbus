package com.proptiger.data.service.b2b;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.proptiger.data.repo.b2b.CatchmentProjectDao;
import com.proptiger.exception.ResourceAlreadyExistException;

@Service
public class CatchmentService {
    Logger                      logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CatchmentDao        catchmentDao;

    @Autowired
    private CatchmentProjectDao catchmentProjectDao;

    @Transactional
    public Catchment createCatchment(Catchment catchment, UserInfo userInfo) {
        catchment.setUserId(userInfo.getUserIdentifier());
        for (CatchmentProject catchmentProject : catchment.getCatchmentProjects()) {
            catchmentProject.setCatchment(catchment);
        }
        try {
            return catchmentDao.save(catchment);
        }
        catch (ConstraintViolationException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        catch (PersistenceException e) {
            if (e.getCause() != null && e.getCause().getCause() instanceof MySQLIntegrityConstraintViolationException) {
                e.printStackTrace();
                throw new ResourceAlreadyExistException(
                        "Catchment name " + catchment.getName() + " already taken",
                        ResponseCodes.CATCHMENTNAME_TAKEN);
            }
            throw new RuntimeException("Unexpected Error");
        }
    }

    public Catchment updateCatchment(Catchment catchment, UserInfo userInfo) {
        Catchment updatedCatchment = null;
        try {
            updatedCatchment = updateExistingCatchment(catchment, userInfo);
        }
        catch (Exception e) {
            if (e.getCause() != null && e.getCause().getCause() instanceof MySQLIntegrityConstraintViolationException) {
                throw new ResourceAlreadyExistException(
                        "Catchment name " + catchment.getName() + " already taken",
                        ResponseCodes.CATCHMENTNAME_TAKEN);
            }
        }
        return updatedCatchment;
    }

    @Transactional
    public Catchment updateExistingCatchment(Catchment catchment, UserInfo userInfo) throws Exception {
        catchment.setUserId(userInfo.getUserIdentifier());
        Catchment savedCatchment = catchmentDao.findOne(catchment.getId());
        if (savedCatchment.getUserId().equals(catchment.getUserId())) {
            List<Integer> oldProjectIds = savedCatchment.getProjectIds();
            List<Integer> newProjectIds = catchment.getProjectIds();

            List<Integer> projectsToBeRemoved = new ArrayList<>(oldProjectIds);
            projectsToBeRemoved.removeAll(newProjectIds);

            savedCatchment.addProjectIds(newProjectIds);
            List<CatchmentProject> deletedCatchmentProjects = savedCatchment.deleteProjectIds(projectsToBeRemoved);

            catchmentProjectDao.delete(deletedCatchmentProjects);

            savedCatchment.setName(catchment.getName());
            savedCatchment.setUpdatedAt(null);
            for (CatchmentProject catchmentProject : savedCatchment.getCatchmentProjects()) {
                if (catchmentProject.getCatchment() == null) {
                    catchmentProject.setCatchment(savedCatchment);
                }
            }
            catchmentDao.save(savedCatchment);
            return savedCatchment;
        }
        return null;
    }

    public List<Catchment> getCatchment(FIQLSelector fiqlSelector) {
        return catchmentDao.getFilteredCatchments(fiqlSelector);
    }
}