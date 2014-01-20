package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.proptiger.data.model.B2b;
import com.proptiger.data.model.Locality;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.B2bDao;
import com.proptiger.data.repo.LocalityDao;

/**
 * @author Azitabh Ajit
 * 
 */

public class B2bService {
	@Autowired
	private B2bDao b2bDao;
	
	public List<B2b> getFilteredDocuments(Selector selector) {
		return Lists.newArrayList(b2bDao.getFilteredDocuments(selector).getResults());
	}
	
}
