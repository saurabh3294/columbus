package com.proptiger.data.service.marketplace;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.pojo.response.PaginatedResponse;
import com.proptiger.core.util.Constants;
import com.proptiger.data.model.MasterLeadOfferStatus;
import com.proptiger.data.repo.marketplace.MasterLeadOfferStatusDao;

@Service
public class MasterLeadOfferStatusService {

    @Autowired
    private MasterLeadOfferStatusDao masterLeadOfferStatusDao;

    @Cacheable(Constants.CacheName.CACHE)
    public boolean getClaimedFlag(int statusId) {
        MasterLeadOfferStatus masterLeadOfferStatus = masterLeadOfferStatusDao.findById(statusId);
        return masterLeadOfferStatus.isClaimed();
    }

    @Cacheable(Constants.CacheName.CACHE)
    public PaginatedResponse<List<MasterLeadOfferStatus>> get(FIQLSelector selector) {
        List<MasterLeadOfferStatus> masterLeadOfferStatuses = masterLeadOfferStatusDao.findAll();
        PaginatedResponse<List<MasterLeadOfferStatus>> paginatedResponse = new PaginatedResponse<>(
                masterLeadOfferStatuses,
                masterLeadOfferStatuses.size());
        return paginatedResponse;
    }

    /**
     * returns master lead offer status based on id
     * 
     * @param id
     * @return
     */
    public MasterLeadOfferStatus getLeadOfferStatus(int id) {
        return masterLeadOfferStatusDao.findById(id);
    }
}