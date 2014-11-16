package com.proptiger.data.service.seo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.model.seo.SeoURLs;
import com.proptiger.data.model.seo.URLCategories;
import com.proptiger.data.repo.seo.SeoURLsDao;

@Service
public class SeoURLService {
    @Autowired
    private SeoURLsDao seoURLsDao;

    @Transactional
    public SeoURLs saveUrls(String url, int objectId, URLCategories urlCategories) {
        SeoURLs seoURLs = new SeoURLs(url, urlCategories, objectId);
        return seoURLsDao.save(seoURLs);
    }
}
