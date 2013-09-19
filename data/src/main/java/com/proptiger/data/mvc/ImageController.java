package com.proptiger.data.mvc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.proptiger.data.model.DomainObject;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.model.image.ImageUpload;
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
	
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody Object getImages(
    			@RequestParam(value = "objectType") String objectType,
    			@RequestParam(required=false, value = "imageType") String imageType,
    			@RequestParam(value = "objectId") String objectId
    		) {
        List<Image> images = imageService.getImages(DomainObject.valueOf(objectType), imageType, Integer.parseInt(objectId));
        return super.filterFields(new ProAPISuccessResponse(images), null);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody Object putImages(@RequestParam("image") MultipartFile image) {
    	File newFile = new File("/home/yugal/image_uploads/" + image.getOriginalFilename());
    	try {
    		if(imageService.isValidImage(image)) {
    			image.transferTo(newFile);	
    		}
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
    	return super.filterFields(new ProAPISuccessResponse(), null);
    }
}
