package com.proptiger.data.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;

public class ImageUtil {

	public static String fileMd5Hash(File file) throws FileNotFoundException,
			IOException {
		int nread = 0;
		MessageDigest md;
		byte[] dataBytes = new byte[1024];
		StringBuffer sb = new StringBuffer();
		try {
			md = MessageDigest.getInstance("MD5");
			FileInputStream fis = new FileInputStream(file);
			while ((nread = fis.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, nread);
			}
			;
			byte[] mdbytes = md.digest();
			for (int i = 0; i < mdbytes.length; i++) {
				sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static boolean isValidImage(File file) {
		try {
			ImageInputStream iis = ImageIO.createImageInputStream(file);
			Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
			if (!iter.hasNext())
				return false;
			ImageReader reader = iter.next();
			String type = reader.getFormatName();
			iis.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static HashMap<String, Object> getImageInfo(File imageFile) throws IOException, ImageProcessingException {
		HashMap<String, Object> info = new HashMap<String, Object>();
		ImageInputStream iis = ImageIO.createImageInputStream(imageFile);
		BufferedImage image = ImageIO.read ( iis );
		info.put("width", String.valueOf(image.getWidth()));    // Width
		info.put("height", String.valueOf(image.getHeight()));  // Height
		// Date
		Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
		if(metadata.containsDirectory(ExifSubIFDDirectory.class)) {
			ExifSubIFDDirectory directory = metadata.getDirectory(ExifSubIFDDirectory.class);
			Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
			info.put("datetime", date);
		} else {
			info.put("datetime", null);
		}
		// Size
		info.put("size_in_bytes", imageFile.length());
		return info;
	}

}
