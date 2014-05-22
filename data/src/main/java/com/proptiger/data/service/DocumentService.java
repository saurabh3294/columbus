package com.proptiger.data.service;

import org.springframework.stereotype.Service;

import com.proptiger.data.enums.MediaType;

@Service
public class DocumentService extends MediaService {
    public DocumentService() {
        mediaType = MediaType.Document;
    }
}