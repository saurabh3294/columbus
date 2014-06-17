package com.proptiger.data.mvc;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.proptiger.data.enums.DomainObject;
import com.proptiger.data.enums.ImageResolution;
import com.proptiger.data.init.ExclusionAwareBeanUtilsBean;
import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.response.APIResponse;
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
    private ImageService       imageService;

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping
    public @ResponseBody
    Object getImages(@RequestParam(required = false) String selector, @RequestParam String objectType, @RequestParam(
            required = false) String imageType, @RequestParam long objectId) {
        List<Image> images = imageService.getImages(DomainObject.valueOf(objectType), imageType, objectId);

        Selector imageSelector = new Selector();
        if (selector != null) {
            imageSelector = super.parseJsonToObject(selector, Selector.class);
        }

        return new APIResponse(super.filterFields(images, imageSelector.getFields()));
    }

    @DisableCaching
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    Object putImages(
            @RequestParam String objectType,
            @RequestParam long objectId,
            @RequestParam MultipartFile image,
            @RequestParam(required = false) Boolean addWaterMark,
            @RequestParam String imageType,
            @ModelAttribute Image imageParams) throws Exception {
        DomainObject domainObject = DomainObject.valueOf(objectType);
        Image img = imageService.uploadImage(
                domainObject,
                imageType,
                objectId,
                image,
                addWaterMark,
                imageParams);
        return new APIResponse(super.filterFields(img, null));
    }

    @DisableCaching
    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    public @ResponseBody
    Object updateImage(
            @PathVariable long id,
            @RequestParam(required = false, value = "image") MultipartFile file,
            @ModelAttribute Image imageParams) throws Exception {
        Image image = imageService.getImage(id);
        Object obj = null;
        Image newUpdateImage = new Image();

        if (file == null || file.isEmpty()) {
            try {
                BeanUtilsBean beanUtilsBean = new ExclusionAwareBeanUtilsBean();
                beanUtilsBean.copyProperties(image, imageParams);
            }
            catch (IllegalAccessException | InvocationTargetException e) {
            }
            imageService.update(image);
            obj = new APIResponse(super.filterFields(image, null));
        }
        else {
            try {
                image.setId(0);
                BeanUtilsBean beanUtilsBean = new ExclusionAwareBeanUtilsBean();
                beanUtilsBean.copyProperties(newUpdateImage, image);
                beanUtilsBean.copyProperties(newUpdateImage, imageParams);
                newUpdateImage.setId(0);
            }
            catch (IllegalAccessException | InvocationTargetException e) {
            }

            obj = applicationContext.getBean(ImageController.class).putImages(
                    image.getImageTypeObj().getObjectType().getType(),
                    image.getObjectId(),
                    file,
                    !image.getWaterMarkHash().equals(image.getOriginalHash()),
                    image.getImageTypeObj().getType(),
                    newUpdateImage);

            imageService.deleteImage(id);
        }

        return obj;
    }

    @DisableCaching
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    Object deleteImage(@PathVariable long id) {
        imageService.deleteImage(id);
        return new APIResponse();
    }

    @RequestMapping(value = "resolution-enumerations")
    public @ResponseBody
    Object getResolutionEnumerations() {
        return new APIResponse(ImageResolution.values());
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // true passed to CustomDateEditor constructor means convert empty String to null
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new ISO8601DateFormat(), true));
    }
}
