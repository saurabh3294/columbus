/**
 * 
 */
package com.proptiger.data.repo;

import java.io.File;

import com.proptiger.core.enums.DomainObject;
import com.proptiger.core.model.proptiger.Image;

/**
 * @author mandeep
 * 
 */
public interface ImageCustomDao {
    public Image insertImage(
            DomainObject objectStr,
            String imageTypeStr,
            long objectId,
            File orignalImage,
            File watermarkImage,
            Image imageObj,
            String format, String originalHash);

    public void markImageAsActive(Image image);
}
