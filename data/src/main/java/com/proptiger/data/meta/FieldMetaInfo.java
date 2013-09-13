package com.proptiger.data.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A program element annotated with FieldMetaInfo is one that will be parsed to
 * read meta information of a field in model classes.
 * 
 * @author Rajeev Pandey
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldMetaInfo {
	public static final String DEF_NAME = "Default";
	String name() default DEF_NAME;
	String displayName();
	String description() ;
	DataType dataType() default DataType.DEFAULT;
	boolean editable() default false;
}
