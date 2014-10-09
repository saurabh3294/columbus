package com.proptiger.data.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Intercepted {
    public @interface ProjectListing{};
    public @interface LocalityListing{};
    public @interface CityListing{};
    public @interface TypeaheadListing{};
    public @interface Trend{};
}
