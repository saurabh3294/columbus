package com.proptiger.app.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.DispatcherServlet;

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
        servletContext.addFilter("etagFilter", new ShallowEtagHeaderFilter()).addMappingForUrlPatterns(null, false, "/*");
    }

}
