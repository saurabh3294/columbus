package com.proptiger.data.model.image;
 
import org.springframework.web.multipart.MultipartFile;
 
public class ImageUpload {
 
	private MultipartFile image;

	/**
	 * @return the image
	 */
	public MultipartFile getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(MultipartFile image) {
		this.image = image;
	}

}

