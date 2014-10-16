package com.proptiger.search.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.DispatcherServlet;

import com.proptiger.core.util.Constants;

/**
 * Initialize web application, configuring front controller DispatcherServlet,
 * this file is replacement of web.xml
 * 
 * @author Rajeev Pandey
 *
 */
public class WebInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        /*
         * As per spring doc this need to be done to load context but doing this
         * every beans created twice, so commenting this as of now to test if
         * every thing works fine.
         */
        rootContext.register(WebMvcConfig.class);

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(
                rootContext));
        dispatcher.addMapping("/");
        dispatcher.setLoadOnStartup(1);
        /*
         * Adding default ShallowEtagHeaderFilter as this class calculates etag
         * value based on byte value from response object so that byte value
         * will have actual values as JSON returned in response, so internal
         * object address will not be used to create etag values.
         */
        servletContext.addFilter("etagFilter", new ShallowEtagHeaderFilter()).addMappingForUrlPatterns(
                null,
                false,
                "/*");
        servletContext.getSessionCookieConfig().setMaxAge(Constants.Security.JSESSION_COOKIE_MAX_AGE);
        
        servletContext.addListener(new RequestContextListener());
    }

}