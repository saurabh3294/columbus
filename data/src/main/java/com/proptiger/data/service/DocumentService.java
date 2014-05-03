package com.proptiger.data.service;

import org.springframework.stereotype.Service;

@Service
public class DocumentService extends MediaService {
    public DocumentService() {
        mediaTypeId = 2;
    }
}
