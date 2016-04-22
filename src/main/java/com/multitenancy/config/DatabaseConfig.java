package com.multitenancy.config;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.multitenancy.persistence.MultiTenantConnectionProviderFactory;
import com.multitenancy.persistence.RequestBasedCurrentTenantIdentifierResolver;

@Configuration
@PropertySource("classpath:/db-config.properties")
@EnableTransactionManagement
public class DatabaseConfig {
    private @Autowired Environment env;

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setDataSource(dataSource());
        entityManager.setPackagesToScan("com.multitenancy.entity");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManager.setJpaVendorAdapter(vendorAdapter);
        entityManager.setJpaPropertyMap(hibernateProperties());
        return entityManager;
    }

    @Bean
    public MultiTenantConnectionProviderFactory tenantConnectionProvider() {
        ConcurrentMap<String, BasicDataSource> dataSourceMap = new ConcurrentHashMap<String, BasicDataSource>();
        dataSourceMap.put("default", dataSource());

        MultiTenantConnectionProviderFactory tenantConnectionProvider = new MultiTenantConnectionProviderFactory();
        tenantConnectionProvider.setDataSourceMap(dataSourceMap);

        return tenantConnectionProvider;
    }

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public RequestBasedCurrentTenantIdentifierResolver tenantIdentifierResolver() {
        RequestBasedCurrentTenantIdentifierResolver tenantIdentifierResolver = new RequestBasedCurrentTenantIdentifierResolver();
        return tenantIdentifierResolver;
    }

    @Bean
    public BasicDataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("db.driver"));
        dataSource.setUrl(env.getProperty("db.url"));
        dataSource.setUsername(env.getProperty("db.username"));
        dataSource.setPassword(env.getProperty("db.password"));
        dataSource.setMaxIdle(10);
        dataSource.setMaxWaitMillis(1000);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDefaultAutoCommit(true);
        dataSource.setValidationQuery("SELECT 1+1");
        dataSource.setTestOnBorrow(true);

        return dataSource;
    }

    private Map<String, Object> hibernateProperties() {
        Map<String, Object> hibernateProperties = new HashMap<String, Object>();

        hibernateProperties.put("hibernate.dialect", "com.multitenancy.persistence.dialect.JsonbPostgreSQLDialect");
        hibernateProperties.put("hibernate.query.substitutions", "true 'Y', false 'N'");
        hibernateProperties.put("hibernate.show_sql", "true");
        hibernateProperties.put("hibernate.format_sql", "true");
        hibernateProperties.put("hibernate.multiTenancy", "DATABASE");
        hibernateProperties.put("hibernate.tenant_identifier_resolver", tenantIdentifierResolver());
        hibernateProperties.put("hibernate.multi_tenant_connection_provider", tenantConnectionProvider());

        return hibernateProperties;
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        HibernateExceptionTranslator hibernateExceptionTranslator = new HibernateExceptionTranslator();
        return hibernateExceptionTranslator;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
