package com.proptiger.data.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.im4java.core.Info;
import org.im4java.core.InfoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.proptiger.core.exception.ProAPIException;
import com.proptiger.core.model.proptiger.Image;
import com.proptiger.core.model.proptiger.Media;

/**
 * @author yugal
 * 
 * @author azi
 * 
 */
public class MediaUtil {

    private static Logger  logger = LoggerFactory.getLogger(MediaUtil.class);
    public static String[] endpoints;
    public static String   bucket;

    /** TODO :: Consider using DigestUtils.md5Hex(InputStream is) here **/
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
            }

            byte[] mdbytes = md.digest();
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            fis.close();
        }
        catch (NoSuchAlgorithmException e) {
            // TODO Add logging
        }

        return sb.toString();
    }

    public static String getImageFormat(File file) throws IllegalArgumentException, IOException {
        ImageInputStream iis = ImageIO.createImageInputStream(file);
        Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
        if (!iter.hasNext())
            throw new IllegalArgumentException("Invalid Image");
        ImageReader reader = iter.next();
        String format = reader.getFormatName();
        iis.close();
        return format.toLowerCase();
    }

    public static void populateImageMetaInfo(File imageFile, Image image) throws IOException, InfoException {
        Info info = new Info(imageFile.getAbsolutePath());
        image.setWidth(info.getImageWidth());
        image.setHeight(info.getImageHeight());

        // Size
        image.setSizeInBytes(imageFile.length());

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
            if (metadata.containsDirectory(ExifSubIFDDirectory.class)) {
                ExifSubIFDDirectory directory = metadata.getDirectory(ExifSubIFDDirectory.class);
                Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                if(image.getTakenAt() == null){
                    image.setTakenAt((date != null) ? (Date) date : null);
                }
            }

            // Latitude - Longitude
            GpsDirectory gpsDirectory = metadata.getDirectory(GpsDirectory.class);
            if (gpsDirectory != null) {
                GeoLocation geo = gpsDirectory.getGeoLocation();
                Double lat = null, lng = null;
                if (geo != null && !geo.isZero()) {
                    lat = geo.getLatitude();
                    lng = geo.getLongitude();
                }
                image.setLatitude(lat);
                image.setLongitude(lng);
            }
        }
        catch (ImageProcessingException e) {
            logger.debug("Unable to get Exif reader : {}", imageFile.getAbsolutePath());
        }

    }

    public static void populateBasicMediaAttributes(File file, Media media) {
        try {
            BasicFileAttributes fileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            media.setSizeInBytes(fileAttributes.size());
            media.setContentHash(fileMd5Hash(file));
        }
        catch (IOException e) {
            throw new ProAPIException("Error Fetching Basic FileAttributes", e);
        }
    }

    public static String getMediaEndpoint(long id) {
        return endpoints[(int) (id % endpoints.length)];
    }
}
