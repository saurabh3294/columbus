package com.proptiger.data.service.user;

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
import com.proptiger.core.constants.ResponseCodes;
import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.data.model.Catchment;
import com.proptiger.data.model.CatchmentProject;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.user.CatchmentDao;
import com.proptiger.data.repo.user.CatchmentProjectDao;
import com.proptiger.exception.ResourceAlreadyExistException;
import com.proptiger.exception.UnauthorizedException;

@Service
public class CatchmentService {
    Logger                      logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CatchmentDao        catchmentDao;

    @Autowired
    private CatchmentProjectDao catchmentProjectDao;

    @Transactional
    public Catchment createCatchment(Catchment catchment, ActiveUser userInfo) {
        catchment.setUserId(userInfo.getUserIdentifier());
        for (CatchmentProject catchmentProject : catchment.getCatchmentProjects()) {
            catchmentProject.setCatchment(catchment);
        }
        try {
            return catchmentDao.save(catchment);
        }
        catch (ConstraintViolationException e) {
            throw new IllegalArgumentException(e.getConstraintViolations().iterator().next().getMessage(), e);
        }
        catch (PersistenceException e) {
            if (e.getCause() != null && e.getCause().getCause() instanceof MySQLIntegrityConstraintViolationException) {
                e.printStackTrace();
                throw new ResourceAlreadyExistException(
                        "Catchment name " + catchment.getName() + " already taken",
                        ResponseCodes.NAME_ALREADY_EXISTS);
            }
            throw new RuntimeException("Unexpected Error");
        }
    }

    public Catchment updateCatchment(Catchment catchment, ActiveUser userInfo) {
        Catchment updatedCatchment = null;
        try {
            updatedCatchment = updateExistingCatchment(catchment, userInfo);
        }
        catch (UnauthorizedException e) {
            throw new UnauthorizedException();
        }
        catch (Exception e) {
            if (e.getCause() != null && e.getCause().getCause() instanceof MySQLIntegrityConstraintViolationException) {
                throw new ResourceAlreadyExistException(
                        "Catchment name " + catchment.getName() + " already taken",
                        ResponseCodes.NAME_ALREADY_EXISTS);
            }
        }
        return updatedCatchment;
    }

    @Transactional
    public Catchment updateExistingCatchment(Catchment catchment, ActiveUser userInfo) throws Exception {
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
            savedCatchment.setMetaAttributes(catchment.getMetaAttributes());
            for (CatchmentProject catchmentProject : savedCatchment.getCatchmentProjects()) {
                if (catchmentProject.getCatchment() == null) {
                    catchmentProject.setCatchment(savedCatchment);
                }
            }
            catchmentDao.save(savedCatchment);
            return savedCatchment;
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public List<Catchment> getCatchment(FIQLSelector fiqlSelector) {
        return catchmentDao.getFilteredCatchments(fiqlSelector);
    }

    public String getCatchmentFIQLFilter(Integer catchmentId, ActiveUser userInfo) {
        FIQLSelector selector = new FIQLSelector();
        Catchment catchment = catchmentDao.findOne(catchmentId);
        if (!catchment.getUserId().equals(userInfo.getUserIdentifier())) {
            throw new UnauthorizedException();
        }
        for (Integer projectId : catchment.getProjectIds()) {
            selector.addOrConditionToFilter("projectId==" + projectId);
        }
        return selector.getFilters();
    }
}