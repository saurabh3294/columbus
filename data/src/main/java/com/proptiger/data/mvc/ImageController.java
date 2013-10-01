package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.proptiger.data.model.DomainObject;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.ImageService;

/**
 * @author yugal
 *
 */
@Controller
@RequestMapping(value="data/v1/entity/image")
public class ImageController extends BaseController {
	@Autowired
	private ImageService imageService;
	
    @RequestMapping
    public @ResponseBody Object getImages(
    			@RequestParam(value = "objectType") String objectType,
    			@RequestParam(required=false, value = "imageType") String imageType,
    			@RequestParam(value = "objectId") long objectId
    		) {
        List<Image> images = imageService.getImages(DomainObject.valueOf(objectType), imageType, objectId);
        return super.filterFields(new ProAPISuccessResponse(images), null);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody Object putImages(
				@RequestParam(value = "objectType") String objectType,
				@RequestParam(value = "imageType") String imageType,
				@RequestParam(value = "objectId") long objectId,
    			@RequestParam("image") MultipartFile image
    		) {
    	Image img = imageService.uploadImage(DomainObject.valueOf(objectType), imageType, objectId, image);
    	return super.filterFields(new ProAPISuccessResponse(img), null);
    }
}
