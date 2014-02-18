package com.proptiger.data.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A program element annotated ResourceMetaInfo is one that will be parsed to
 * read meta information contained in that class/resource.
 * 
 * @author Rajeev Pandey
 * 
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceMetaInfo {
    String name() default "";
}
