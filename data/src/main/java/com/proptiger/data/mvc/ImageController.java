package com.proptiger.data.mvc;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.proptiger.data.init.NullAwareBeanUtilsBean;
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
    Object getImages(@RequestParam(required = false) String selector, @RequestParam String objectType,
            @RequestParam(required=false) String imageType, @RequestParam long objectId)
    {
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
    Object putImages(@RequestParam String objectType, @RequestParam long objectId, @RequestParam MultipartFile image,
            @RequestParam(required = false) Boolean addWaterMark, @RequestParam String imageType,
            @ModelAttribute Image imageParams) {
        DomainObject domainObject = DomainObject.valueOf(objectType);
        int domainObjectValueStart = domainObject.getStartId();
        long normalizedObjectId = objectId;
        if (objectId > domainObjectValueStart) {
            normalizedObjectId = objectId - domainObjectValueStart;
        }

        Image img = imageService
                .uploadImage(domainObject, imageType, normalizedObjectId, image, addWaterMark, imageParams);
        return new ProAPISuccessResponse(super.filterFields(img, null));
    }

    @DisableCaching
    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    public @ResponseBody
    Object updateImage(@PathVariable long id, @RequestParam(required=false, value = "image") MultipartFile file,
            @ModelAttribute Image imageParams) {
        Image image = imageService.getImage(id);

        Object obj = null;

        if (file == null || file.isEmpty()) {
            try {
                BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
                beanUtilsBean.copyProperties(image, imageParams);
            } catch (IllegalAccessException | InvocationTargetException e) {
            }            
            imageService.update(image);
            obj = new ProAPISuccessResponse(super.filterFields(image, null));
        }
        else {
            try {
                image.setId(0);
                BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
                beanUtilsBean.copyProperties(imageParams, image);
                image.setId(id);
            } catch (IllegalAccessException | InvocationTargetException e) {
            }
            
            obj = this.putImages(image.getImageTypeObj().getObjectType().getType(), image.getObjectId(),
                    file, !image.getWaterMarkHash().equals(image.getOriginalHash()), image.getImageTypeObj().getType(), imageParams);

            imageService.deleteImage(id);
        }

        return obj;
    }

    @DisableCaching
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    Object deleteImage(@PathVariable long id) {
        imageService.deleteImage(id);
        return new ProAPISuccessResponse();
    }

    @RequestMapping(value = "resolution-enumerations")
    public @ResponseBody
    Object getResolutionEnumerations() {
        return new ProAPISuccessResponse(ImageResolution.values());
    }
}
