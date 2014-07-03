package com.proptiger.data.mvc;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.proptiger.data.enums.DomainObject;
import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.Media;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.AudioService;
import com.proptiger.data.service.DocumentService;
import com.proptiger.data.util.FileUtil;

/**
 * 
 * @author azi
 * 
 */
@Controller
@DisableCaching
@RequestMapping(value = "data/v1/entity")
public class MediaController {
    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private AudioService audioService;

    @RequestMapping(method = RequestMethod.POST, value = "/document")
    public @ResponseBody
    Object createMedia(
            @RequestParam DomainObject objectType,
            @RequestParam Integer objectId,
            @RequestParam MultipartFile file,
            @RequestParam String documentType,
            @ModelAttribute Media media) {
        File tempFile  = FileUtil.createFileFromMultipartFile(file);
        return documentService.createMedia(objectType, objectId, tempFile , documentType, media);
    }

    @RequestMapping(value = "/document")
    @ResponseBody
    public APIResponse getMedia(
            @RequestParam DomainObject objectType,
            @RequestParam Integer objectId,
            @RequestParam(required = false) String documentType) {
        return new APIResponse(documentService.getMedia(objectType, objectId, documentType));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/document/{id}")
    @ResponseBody
    public APIResponse deleteMedia(@PathVariable Integer id) {
        documentService.deleteMedia(id);
        return new APIResponse();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/document/{id}")
    @ResponseBody
    public APIResponse updateMedia(@PathVariable Integer id, @ModelAttribute Media media) {
        return new APIResponse(documentService.updateMedia(media, id));
    }
    
    /********** AUDIO ************/
    
    @RequestMapping(method = RequestMethod.POST, value = "/audio")
    public @ResponseBody
    Object createMediaAudio(
            @RequestParam DomainObject objectType,
            @RequestParam Integer objectId,
            @RequestParam MultipartFile file,
            @RequestParam String documentType,
            @ModelAttribute Media media) {
        File tempFile  = FileUtil.createFileFromMultipartFile(file);
        return audioService.createMedia(objectType, objectId, tempFile , documentType, media);
    }

    @RequestMapping(value = "/audio")
    @ResponseBody
    public APIResponse getMediaAudio(
            @RequestParam DomainObject objectType,
            @RequestParam Integer objectId,
            @RequestParam(required = false) String documentType) {
        return new APIResponse(audioService.getMedia(objectType, objectId, documentType));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/audio/{id}")
    @ResponseBody
    public APIResponse deleteMediaAudio(@PathVariable Integer id) {
        audioService.deleteMedia(id);
        return new APIResponse();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/audio/{id}")
    @ResponseBody
    public APIResponse updateMediaAudio(@PathVariable Integer id, @RequestBody Media media) {
        return new APIResponse(audioService.updateMedia(media, id));
    }
}