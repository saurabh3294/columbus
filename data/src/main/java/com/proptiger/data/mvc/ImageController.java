package com.proptiger.data.mvc;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.enums.DomainObject;
import com.proptiger.data.model.enums.ImageResolution;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.ImageService;

/**
 * @author yugal
 * 
 */
@Controller
@DisableCaching
@RequestMapping(value = "data/v1/entity/image")
public class ImageController extends BaseController {
    @Autowired
    private ImageService imageService;

    @RequestMapping
    public @ResponseBody
    Object getImages(@RequestParam(required = false) String selector,
            @RequestParam(value = "objectType") String objectType,
            @RequestParam(required = false, value = "imageType") String imageType,
            @RequestParam(value = "objectId") long objectId) {
        List<Image> images = imageService.getImages(DomainObject.valueOf(objectType), imageType, objectId);

        Selector imageSelector = new Selector();
        if (selector != null) {
            imageSelector = super.parseJsonToObject(selector, Selector.class);
        }

        return new ProAPISuccessResponse(super.filterFields(images, imageSelector.getFields()));
    }

    @DisableCaching
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    Object putImages(@RequestParam String objectType, @RequestParam String imageType, @RequestParam long objectId,
            @RequestParam MultipartFile image, @RequestParam(required = false) Boolean addWaterMark,
            @RequestParam(required = false) String altText, @RequestParam(required = false) String title,
            @RequestParam(required = false) String description, @RequestParam(required = false) String priority) {
        Map<String, String> extraInfo = new HashMap<String, String>();
        extraInfo.put("altText", altText);
        extraInfo.put("title", title);
        extraInfo.put("description", description);
        extraInfo.put("priority", priority);
        
        DomainObject domainObject = DomainObject.valueOf(objectType);
        int domainObjectValueStart = domainObject.getStartId();
        long normalizedObjectId = objectId;
        if (objectId > domainObjectValueStart) {
        	normalizedObjectId = objectId - domainObjectValueStart;
        }
        
        Image img = imageService.uploadImage(domainObject, imageType, normalizedObjectId, image,
                addWaterMark, extraInfo);
        return new ProAPISuccessResponse(super.filterFieldsWithTree(img, null));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    Object deleteImage(@PathVariable long id) {
        imageService.deleteImage(id);
        return new ProAPISuccessResponse();
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    public @ResponseBody
    Object updateImage(@PathVariable long id, @RequestParam(value="image") MultipartFile file) {
        Image image = imageService.getImage(id);
        Object obj = this.putImages(image.getImageType().getObjectType().getType(), image.getImageType().getType(), image.getObjectId(), file, !image.getWaterMarkHash().equals(image.getOriginalHash()), image.getAltText(), image.getTitle(), image.getDescription(), image.getPriority() == null ? "" : String.valueOf(image.getPriority()));
        imageService.deleteImage(id);
        return obj;
    }

    @RequestMapping(value="resolution-enumerations")
    public @ResponseBody Object getResolutionEnumerations() {
        return new ProAPISuccessResponse(ImageResolution.values());
    }

    @RequestMapping(value="create-new", method = RequestMethod.POST)
    public @ResponseBody
    Object createNewImages(@RequestParam long imageId, @RequestParam MultipartFile image) {
        Map<String, String> extraInfo = new HashMap<String, String>();
        extraInfo.put("altText", null);
        extraInfo.put("title", null);
        extraInfo.put("description", null);
        extraInfo.put("priority", null);
        
        Image img = imageService.createNewImage(imageId, image);
        return new ProAPISuccessResponse(super.filterFieldsWithTree(img, null));
    }    
}
