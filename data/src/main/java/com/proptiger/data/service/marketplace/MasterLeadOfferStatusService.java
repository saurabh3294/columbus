package com.proptiger.data.service.marketplace;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.MasterLeadOfferStatus;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.repo.marketplace.MasterLeadOfferStatusDao;

@Service
public class MasterLeadOfferStatusService {

    @Autowired
    private MasterLeadOfferStatusDao masterLeadOfferStatusDao;

    public boolean getClaimedFlag(int statusId) {
        MasterLeadOfferStatus masterLeadOfferStatus = masterLeadOfferStatusDao.findById(statusId);
        return masterLeadOfferStatus.isClaimedFlag();
    }

    public PaginatedResponse<List<MasterLeadOfferStatus>> get(FIQLSelector selector) {
        List<MasterLeadOfferStatus> masterLeadOfferStatuses = masterLeadOfferStatusDao.findAll();
        PaginatedResponse<List<MasterLeadOfferStatus>> paginatedResponse = new PaginatedResponse<>(
                masterLeadOfferStatuses,
                masterLeadOfferStatuses.size());
        return paginatedResponse;
    }

}
