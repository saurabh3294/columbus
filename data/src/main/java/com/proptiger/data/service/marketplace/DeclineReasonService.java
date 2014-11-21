package com.proptiger.data.service.marketplace;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.marketplace.DeclineReason;
import com.proptiger.data.repo.marketplace.DeclineReasonDao;

@Service
public class DeclineReasonService {

    @Autowired
    DeclineReasonDao declineReasonDao;
    
    public DeclineReason getReasonById(int id) {
        return declineReasonDao.findById(id);
    }

    public List<DeclineReason> getAllReasons() {
        return declineReasonDao.findAll();

    }
}
