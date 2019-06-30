package com.jiayi.platform.basic.config;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author : weichengke
 * @date : 2019-03-01 10:55
 */
@Configuration
@EnableJpaRepositories(basePackages = {"com.jiayi.platform.basic.**.dao","com.jiayi.platform.security.**.dao"}, entityManagerFactoryRef = "jpaEntityManagerFactory")
public class MySQLDateSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.mysql")
    public Properties dataSourceProperties() {
        return new Properties();
    }

    @Bean(name = "mysqlDataSource")
    @Primary
    public DataSource mysqlDataSource(Properties properties) throws Exception {
        return DruidDataSourceFactory.createDataSource(properties);
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(@Qualifier("jpaEntityManagerFactory") EntityManagerFactory jpaEntityManagerFactory) {
        return new JpaTransactionManager(jpaEntityManagerFactory);
    }

    @Bean
    @Qualifier("jpaEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean jpaEntityManagerFactory(
            DataSource mysqlDataSource,
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(mysqlDataSource)
                .persistenceUnit("mysql")
                .packages("com.jiayi.platform.basic.**.entity", "com.jiayi.platform.security.**.entity")
                .build();
    }
}
