package com.proptiger.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.Bank;

/**
 * @author Rajeev Pandey
 * 
 */
public interface BankDao extends JpaRepository<Bank, Integer> {

}
