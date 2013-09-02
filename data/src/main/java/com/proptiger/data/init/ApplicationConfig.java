package com.proptiger.data.init;

import java.util.Properties;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.ejb.HibernatePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * This class is responsible to configure Spring Data JPA, will create data source,
 * and entity manager for Spring Data JPA managed entities.
 * 
 * @author Rajeev Pandey
 *
 */
@Configuration
@EnableWebMvc
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

	@Resource
	private Environment env;

	/**
	 * Creating Data source
	 */
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();

		dataSource.setDriverClassName(env
				.getRequiredProperty(DATABASE_DRIVER));
		dataSource.setUrl(env.getRequiredProperty(DATABASE_URL));
		dataSource.setUsername(env
				.getRequiredProperty(DATABASE_USERNAME));
		dataSource.setPassword(env
				.getRequiredProperty(DATABASE_PASSWORD));

		return dataSource;
	}

	@Bean
	@Autowired
	public EntityManagerFactory entityManagerFactory() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setGenerateDdl(false);
		vendorAdapter.setShowSql(false);
		vendorAdapter.setDatabasePlatform(env.getRequiredProperty(HIBERNATE_DIALECT));
		vendorAdapter.setDatabase(Database.MYSQL);

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setDataSource(dataSource());
		factory.setPersistenceProviderClass(HibernatePersistence.class);
		factory.setPackagesToScan(env.getRequiredProperty(ENTITYMANAGER_PACKAGES_TO_SCAN));
		factory.setJpaProperties(hibProperties());
		
		
		factory.afterPropertiesSet();
		return factory.getObject();
	}
	
	private Properties hibProperties() {
		Properties properties = new Properties();
		properties.put(HIBERNATE_DIALECT,
				env.getRequiredProperty(HIBERNATE_DIALECT));
		properties.put(HIBERNATE_SHOW_SQL,
				env.getRequiredProperty(HIBERNATE_SHOW_SQL));
		return properties;
	}

	@Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }
	@Bean
	@Autowired
	public JpaTransactionManager transactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory());
		return transactionManager;
	}

}
