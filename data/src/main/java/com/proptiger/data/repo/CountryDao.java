package com.proptiger.data.repo;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.Country;

public interface CountryDao extends JpaRepository<Country, Serializable>{

    @Query("SELECT C FROM Country C WHERE countryId = ?1")
    public Country getCountryOnId(Integer countryId); 
        
        
}
