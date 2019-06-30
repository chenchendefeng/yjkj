package com.jiayi.platform.judge.config;

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
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = {"com.jiayi.common.core", "com.jiayi.platform.judge.dao.mysql", "com.jiayi.platform.security.core.dao", "com.jiayi.platform.basic.dao"}, entityManagerFactoryRef = "jpaEntityManagerFactory")
@EnableTransactionManagement(order = 8)
public class MySQLDateSourceConfig {

    @Bean(name = "mysqlProperties")
    @Primary
    @ConfigurationProperties("spring.datasource.mysql")
    public Properties dataSourceProperties() {
        return new Properties();
    }

    @Bean(name = "mysqlDataSource")
    @Primary
    public DataSource mysqlDataSource(@Qualifier("mysqlProperties") Properties properties) throws Exception {
        return DruidDataSourceFactory.createDataSource(properties);
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(@Qualifier("jpaEntityManagerFactory") EntityManagerFactory jpaEntityManagerFactory) {
        JpaTransactionManager jpaTransactionManager= new JpaTransactionManager(jpaEntityManagerFactory);
        jpaTransactionManager.setGlobalRollbackOnParticipationFailure(false);
        return jpaTransactionManager;
    }

    @Bean
    @Qualifier("jpaEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean jpaEntityManagerFactory(DataSource mysqlDataSource, EntityManagerFactoryBuilder builder) {
        return builder.dataSource(mysqlDataSource)
                .persistenceUnit("mysql")
//                .packages("com.jiayi.common.core.entity", "com.jiayi.platform.judge.entity.mysql")
                .packages("com.jiayi.platform.judge.entity.mysql","com.jiayi.platform.security.core.entity", "com.jiayi.platform.basic.entity")
                .build();
    }
}
