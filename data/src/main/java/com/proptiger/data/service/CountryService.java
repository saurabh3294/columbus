package com.proptiger.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Country;
import com.proptiger.data.repo.CountryDao;

@Service
public class CountryService {
    
    @Autowired
    CountryDao          countryDao;

    public Country getCountryOnId(Integer countryId) {
        return countryDao.getCountryOnId(countryId);
        
    }
}
