/**
 * 
 */
package com.proptiger.data.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author mandeep
 *
 */
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
public enum ImageResolution {
    THUMBNAIL(130, 100),
    MEDIUM(320, 240),
    LARGE(940, 720),
    VERY_LARGE(1040, 780),
    MOBILE_0(220, 120),
    MOBILE_1(280, 200),
    MOBILE_2(320, 220),
    MOBILE_3(360, 240),
    MOBILE_4(420, 280),
    MOBILE_5(480, 320),
    MOBILE_6(520, 340),
    MOBILE_8(380, 280),
    MOBILE_9(520, 400),
    MOBILE_10(680, 580),
    MOBILE_11(800, 620),
    MOBILE_12(940, 720),
    MOBILE_13(520, 400);

    private String label;
    private int width;
    private int height;
    
    private ImageResolution(int width, int height) {
        this.width = width;
        this.height = height;
        this.label = this.name();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getLabel() {
        return label;
    }
}
