package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    public @ResponseBody ProAPISuccessResponse getProjects() {
        List<Image> images = imageService.getImages(DomainObject.PROJECT, "main", 1);
        return new ProAPISuccessResponse(images);
    }
}
