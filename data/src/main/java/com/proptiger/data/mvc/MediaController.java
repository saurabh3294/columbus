package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.Media;
import com.proptiger.data.model.enums.DomainObject;
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

    @DisableCaching
    @RequestMapping(method = RequestMethod.POST, value = "/document")
    public @ResponseBody
    Object postMedia(
            @RequestParam DomainObject objectType,
            @RequestParam Integer objectId,
            @RequestParam MultipartFile file,
            @RequestParam String objectMediaType,
            @ModelAttribute Media media) {
        return documentService.postMedia(objectType, objectId, file, objectMediaType, media);
    }
}