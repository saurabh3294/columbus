package com.proptiger.app.config;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.ejb.HibernatePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.proptiger.data.init.CustomHibernateJpaVendorAdapter;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;

/**
 * This class is responsible to configure Spring Data JPA, will create data
 * source, and entity manager for Spring Data JPA managed entities.
 * 
 * @author Rajeev Pandey
 * 
 */
@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
@EnableJpaRepositories("com.proptiger.data.repo")
public class AppRepositoryConfig {

    @Autowired
    private PropertyReader              propertyReader;

    private static EntityManagerFactory wordpressEntityFactory;
    private static EntityManagerFactory wordpressNewsEntityFactory;

    @PostConstruct
    protected void init() throws Exception {
        wordpressEntityFactory = createWordpressEntityManagerFactory(propertyReader
                .getRequiredProperty(PropertyKeys.WORDPRESS_DATABASE_URL));
        wordpressNewsEntityFactory = createWordpressEntityManagerFactory(propertyReader
                .getRequiredProperty(PropertyKeys.WORDPRESS_NEWS_DATABASE_URL));
    }

    private EntityManagerFactory createWordpressEntityManagerFactory(String dbUrl) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(propertyReader.getRequiredProperty(PropertyKeys.DATABASE_DRIVER));
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(propertyReader.getRequiredProperty(PropertyKeys.DATABASE_USERNAME));
        dataSource.setPassword(propertyReader.getRequiredProperty(PropertyKeys.DATABASE_PASSWORD));

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(createJPAAdapter());
        factory.setDataSource(dataSource);
        factory.setPersistenceProviderClass(HibernatePersistence.class);
        factory.setPackagesToScan(propertyReader.getRequiredProperty(PropertyKeys.ENTITYMANAGER_PACKAGES_TO_SCAN));
        factory.setJpaProperties(createJPAProperties());
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    /**
     * Spring data source without pooling Creating Data source
     * 
     * @throws Exception
     */
//    @Bean
//    public DataSource dataSource() throws Exception {
//        /*
//         * Spring data source that does not use pooling
//         */
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName(propertyReader.getRequiredProperty(PropertyKeys.DATABASE_DRIVER));
//        dataSource.setUrl(propertyReader.getRequiredProperty(PropertyKeys.DATABASE_URL));
//        dataSource.setUsername(propertyReader.getRequiredProperty(PropertyKeys.DATABASE_USERNAME));
//        dataSource.setPassword(propertyReader.getRequiredProperty(PropertyKeys.DATABASE_PASSWORD));
//
//        return dataSource;
//    }

    /**
     * Creating c3p0 data source with pooling capability. Modify c3p0.properties
     * for pool configurations
     * 
     * @return
     * @throws Exception
     */
    @Bean
    public DataSource pooledDataSource() throws Exception {
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();

        comboPooledDataSource.setJdbcUrl(propertyReader.getRequiredProperty(PropertyKeys.DATABASE_URL));
        comboPooledDataSource.setDriverClass(propertyReader.getRequiredProperty(PropertyKeys.DATABASE_DRIVER));
        comboPooledDataSource.setUser(propertyReader.getRequiredProperty(PropertyKeys.DATABASE_USERNAME));
        comboPooledDataSource.setPassword(propertyReader.getRequiredProperty(PropertyKeys.DATABASE_PASSWORD));

        return comboPooledDataSource;

    }

    @Bean
    @Autowired
    public EntityManagerFactory entityManagerFactory() throws Exception {

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        // set jpa vendor
        factory.setJpaVendorAdapter(createJPAAdapter());
        //factory.setDataSource(dataSource());
        factory.setDataSource(pooledDataSource());
        factory.setPersistenceProviderClass(HibernatePersistence.class);
        factory.setPackagesToScan(propertyReader.getRequiredProperty(PropertyKeys.ENTITYMANAGER_PACKAGES_TO_SCAN));
        factory.setJpaProperties(createJPAProperties());

        factory.afterPropertiesSet();
        return factory.getObject();
    }

    /**
     * Creating hibernate jpa adapter
     * 
     * @return
     */
    private HibernateJpaVendorAdapter createJPAAdapter() {
        CustomHibernateJpaVendorAdapter vendorAdapter = new CustomHibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(Boolean.valueOf(propertyReader.getRequiredProperty(PropertyKeys.HIBERNATE_SHOW_SQL)));
        vendorAdapter.setDatabase(Database.MYSQL);
        return vendorAdapter;
    }

    /**
     * Create JPA properties
     * 
     * @return
     */
    private Properties createJPAProperties() {
        Properties properties = new Properties();
        properties.put(
                PropertyKeys.HIBERNATE_DIALECT,
                propertyReader.getRequiredProperty(PropertyKeys.HIBERNATE_DIALECT));
        return properties;
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }

    @Bean
    @Autowired
    public JpaTransactionManager transactionManager() throws Exception {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory());
        return transactionManager;
    }

    public static EntityManagerFactory getWordpressEntityFactory() {
        return wordpressEntityFactory;
    }

    public static EntityManagerFactory getWordpressNewsEntityFactory() {
        return wordpressNewsEntityFactory;
    }

}
