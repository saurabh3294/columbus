package com.proptiger.data.repo.seo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.seo.SeoURLs;

public interface SeoURLsDao extends JpaRepository<SeoURLs, Integer>{
   
}
