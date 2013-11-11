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

	private static final String MIN_POOL_SIZE = "min.pool.size"; 
	private static final String MAX_POOL_SIZE = "max.pool.size"; 
	private static final String INITIAL_POOL_SIZE = "initial.pool.size"; 
	private static final String ACQUIRE_INCREMENT = "acquire.increment";
	
	@Autowired
	private PropertyReader propertyReader;
	
	/**
	 * Creating Data source
	 * @throws Exception 
	 */
	@Bean
	public DataSource dataSource() throws Exception {
		/*
		 * C3P0 data source
		 */
		ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
		comboPooledDataSource.setJdbcUrl(propertyReader.getRequiredProperty(DATABASE_URL));
		comboPooledDataSource.setDriverClass(propertyReader
				.getRequiredProperty(DATABASE_DRIVER));
		comboPooledDataSource.setUser(propertyReader
				.getRequiredProperty(DATABASE_USERNAME));
		comboPooledDataSource.setPassword(propertyReader
				.getRequiredProperty(DATABASE_PASSWORD));
		
		int minPoolSize = Integer.parseInt(propertyReader.getRequiredProperty(MIN_POOL_SIZE));
		int maxPoolSize = Integer.parseInt(propertyReader.getRequiredProperty(MAX_POOL_SIZE));
		int initialPoolSize = Integer.parseInt(propertyReader.getRequiredProperty(INITIAL_POOL_SIZE));
		int acquireIncrement = Integer.parseInt(propertyReader.getRequiredProperty(ACQUIRE_INCREMENT));
		
		comboPooledDataSource.setMinPoolSize(minPoolSize);
		comboPooledDataSource.setMaxPoolSize(maxPoolSize);
		comboPooledDataSource.setInitialPoolSize(initialPoolSize);
		comboPooledDataSource.setAcquireIncrement(acquireIncrement);
		/*
		 * Spring data source that does not use pooling
		 */
		/*DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(propertyReader
				.getRequiredProperty(DATABASE_DRIVER));
		dataSource.setUrl(propertyReader.getRequiredProperty(DATABASE_URL));
		dataSource.setUsername(propertyReader
				.getRequiredProperty(DATABASE_USERNAME));
		dataSource.setPassword(propertyReader
				.getRequiredProperty(DATABASE_PASSWORD));*/

		return comboPooledDataSource;
	}

	@Bean
	@Autowired
	public EntityManagerFactory entityManagerFactory() throws Exception {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setGenerateDdl(false);
		vendorAdapter.setShowSql(false);
		vendorAdapter.setDatabasePlatform(propertyReader.getRequiredProperty(HIBERNATE_DIALECT));
		vendorAdapter.setDatabase(Database.MYSQL);

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setDataSource(dataSource());
		factory.setPersistenceProviderClass(HibernatePersistence.class);
		factory.setPackagesToScan(propertyReader.getRequiredProperty(ENTITYMANAGER_PACKAGES_TO_SCAN));
		factory.setJpaProperties(hibProperties());
		
		
		factory.afterPropertiesSet();
		return factory.getObject();
	}
	
	private Properties hibProperties() {
		Properties properties = new Properties();
		properties.put(HIBERNATE_DIALECT,
				propertyReader.getRequiredProperty(HIBERNATE_DIALECT));
		properties.put(HIBERNATE_SHOW_SQL,
				propertyReader.getRequiredProperty(HIBERNATE_SHOW_SQL));
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
