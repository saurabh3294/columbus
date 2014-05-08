package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.Media;
import com.proptiger.data.model.enums.DomainObject;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.DocumentService;

/**
 * 
 * @author azi
 * 
 */
@Controller
@DisableCaching
@RequestMapping(value = "data/v1/entity/media")
public class MediaController {
    @Autowired
    private DocumentService documentService;

    @RequestMapping(method = RequestMethod.POST, value = "/document")
    public @ResponseBody
    Object createMedia(
            @RequestParam DomainObject objectType,
            @RequestParam Integer objectId,
            @RequestParam MultipartFile file,
            @RequestParam String documentType,
            @ModelAttribute Media media) {
        return documentService.createMedia(objectType, objectId, file, documentType, media);
    }

    @RequestMapping(value = "/document")
    @ResponseBody
    public ProAPIResponse getMedia(
            @RequestParam DomainObject objectType,
            @RequestParam Integer objectId,
            @RequestParam(required = false) String documentType) {
        return new ProAPISuccessResponse(documentService.getMedia(objectType, objectId, documentType));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/document/{id}")
    @ResponseBody
    public ProAPIResponse deleteMedia(@PathVariable Integer id) {
        documentService.deleteMedia(id);
        return new ProAPISuccessResponse();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/document/{id}")
    @ResponseBody
    public ProAPIResponse updateMedia(@PathVariable Integer id, @ModelAttribute Media media) {
        return new ProAPISuccessResponse(documentService.updateMedia(media, id));
    }
}