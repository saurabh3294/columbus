package com.proptiger.data.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ImageUtil {
	
	public static String fileMd5Hash(File file) throws FileNotFoundException, IOException {
		int nread = 0;
		MessageDigest md;
		byte[] dataBytes = new byte[1024];
		StringBuffer sb = new StringBuffer();
		try {
			md = MessageDigest.getInstance("MD5");
			FileInputStream fis = new FileInputStream(file);
	        while ((nread = fis.read(dataBytes)) != -1) {
	            md.update(dataBytes, 0, nread);
	        };
	        byte[] mdbytes = md.digest();
	        for (int i = 0; i < mdbytes.length; i++) {
	        	sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
	        }
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

}
