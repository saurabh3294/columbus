package com.proptiger.data.init;

import java.util.Properties;

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
import com.proptiger.data.util.PropertyReader;

/**
 * This class is responsible to configure Spring Data JPA, will create data source,
 * and entity manager for Spring Data JPA managed entities.
 * 
 * @author Rajeev Pandey
 *
 */
@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
@EnableJpaRepositories("com.proptiger.data.repo")
public class ApplicationConfig {

	private static final String DATABASE_DRIVER = "db.driver";
	private static final String DATABASE_PASSWORD = "db.password";
	private static final String DATABASE_URL = "db.url";
	private static final String DATABASE_USERNAME = "db.username";

	private static final String HIBERNATE_DIALECT = "hibernate.dialect";
	private static final String HIBERNATE_SHOW_SQL = "hibernate.show_sql";
	private static final String ENTITYMANAGER_PACKAGES_TO_SCAN = "entitymanager.packages.to.scan";

	@Autowired
	private PropertyReader propertyReader;
	
	/**
	 * Spring data source without pooling
	 * Creating Data source
	 * @throws Exception 
	 */
	@Bean
	public DataSource dataSource() throws Exception {
		/*
		 * Spring data source that does not use pooling
		 */
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(propertyReader
				.getRequiredProperty(DATABASE_DRIVER));
		dataSource.setUrl(propertyReader.getRequiredProperty(DATABASE_URL));
		dataSource.setUsername(propertyReader
				.getRequiredProperty(DATABASE_USERNAME));
		dataSource.setPassword(propertyReader
				.getRequiredProperty(DATABASE_PASSWORD));

		return dataSource;
	}

	/**
	 * Creating c3p0 data source with pooling capability.
	 * Modify c3p0.properties for pool configurations
	 * @return
	 * @throws Exception
	 */
	//@Bean
	public DataSource pooledDataSource() throws Exception{
		ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
		
		comboPooledDataSource.setJdbcUrl(propertyReader.getRequiredProperty(DATABASE_URL));
		comboPooledDataSource.setDriverClass(propertyReader
				.getRequiredProperty(DATABASE_DRIVER));
		comboPooledDataSource.setUser(propertyReader
				.getRequiredProperty(DATABASE_USERNAME));
		comboPooledDataSource.setPassword(propertyReader
				.getRequiredProperty(DATABASE_PASSWORD));
		
		return comboPooledDataSource;
		
	}
	
	@Bean
	@Autowired
	public EntityManagerFactory entityManagerFactory() throws Exception {

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		//set jpa vendor
		factory.setJpaVendorAdapter(createJPAAdapter());
		factory.setDataSource(dataSource());
		//factory.setDataSource(pooledDataSource());
		factory.setPersistenceProviderClass(HibernatePersistence.class);
		factory.setPackagesToScan(propertyReader.getRequiredProperty(ENTITYMANAGER_PACKAGES_TO_SCAN));
		factory.setJpaProperties(createJPAProperties());
		
		factory.afterPropertiesSet();
		return factory.getObject();
	}

	/**
	 * Creating hibernate jpa adapter
	 * @return
	 */
	private HibernateJpaVendorAdapter createJPAAdapter() {
		CustomHibernateJpaVendorAdapter vendorAdapter = new CustomHibernateJpaVendorAdapter();
		vendorAdapter.setShowSql(Boolean.valueOf(propertyReader.getRequiredProperty(HIBERNATE_SHOW_SQL)));
		vendorAdapter.setDatabase(Database.MYSQL);
		return vendorAdapter;
	}
	
	/**
	 * Create JPA properties
	 * @return
	 */
	private Properties createJPAProperties() {
		Properties properties = new Properties();
		properties.put(HIBERNATE_DIALECT, propertyReader.getRequiredProperty(HIBERNATE_DIALECT));
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

}
