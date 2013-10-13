/**
 * 
 */
package com.proptiger.data.repo;

import java.io.File;
import java.util.Map;

import com.proptiger.data.model.DomainObject;
import com.proptiger.data.model.image.Image;

/**
 * @author mandeep
 * 
 */
public interface ImageCustomDao {
    public Image insertImage(DomainObject objectStr, String imageTypeStr, long objectId, File orignalImage,
            File watermarkImage, Map<String, String> extraInfo);

    public void markImageAsActive(Image image);
}
