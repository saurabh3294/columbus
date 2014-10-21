package com.proptiger.data.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.B2BAttribute;
import com.proptiger.data.repo.B2BAttributeDao;
import com.proptiger.exception.ProAPIException;

@Service
public class B2BAttributeService {

    @Autowired
    private static Logger logger = LoggerFactory.getLogger(B2BAttributeService.class);

    @Value("${b2b.price-inventory.max.month.dblabel}")
    String                b2bEndDateDbLabel;

    @Autowired
    B2BAttributeDao       b2bAttributeDao;

    Map<String, String>   b2bAttributeMap;

    @PostConstruct
    private void getAttributes() throws ProAPIException {
        b2bAttributeMap = new HashMap<String, String>();
        Iterable<B2BAttribute> b2bPropertyList = b2bAttributeDao.findAll();

        if (b2bPropertyList == null) {
            throw new ProAPIException("Unable to read B2B Attributes from database.");
        }

        for (B2BAttribute b2bp : b2bPropertyList) {
            b2bAttributeMap.put(b2bp.getName(), b2bp.getValue());
        }
    }

    /**
     * @param attribute
     *            name
     * @return attribute value. Null if no attribute is found by that name
     */
    public String getAttributeByName(String attributeName) {
        return b2bAttributeMap.get(attributeName);
    }
}
