package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.enums.LeadOfferStatus;
import com.proptiger.data.model.Company;
import com.proptiger.data.model.marketplace.Lead;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.model.seller.CompanyUser;
import com.proptiger.data.repo.marketplace.LeadOfferDao;
import com.proptiger.exception.ProAPIException;

/**
 * 
 * @author azi
 *
 */

@Service
public class LeadOfferService {
    @Autowired
    private CompanyService companyService;

    @Autowired
    private LeadOfferDao   leadOfferDao;

    public LeadOffer offerLeadToBroker(Lead lead, Company brokerCompany, int cycleId) {
        List<CompanyUser> agents = companyService.getCompanyUsersForCompanies(brokerCompany);
        if (agents.size() == 0) {
            throw new ProAPIException("No Agent Found For Broker");
        }
        else {
            return createLeadOffer(lead, agents.get(0));
        }
    }

    public LeadOffer createLeadOffer(Lead lead, CompanyUser agent) {
        LeadOffer offer = new LeadOffer();
        offer.setLeadId(lead.getId());
        offer.setAgentId(agent.getId());
        offer.setStatusId(LeadOfferStatus.Offered.getLeadOfferStatusId());
        offer.setCycleId(1);
        return leadOfferDao.save(offer);
    }
}
