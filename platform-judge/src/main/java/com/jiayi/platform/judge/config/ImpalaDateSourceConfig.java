package com.jiayi.platform.judge.config;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@MapperScan(basePackages = {"com.jiayi.platform.judge.dao.impala", "com.jiayi.platform.library.minerepo.dao", "com.jiayi.platform.library.monrepo.dao"},sqlSessionFactoryRef = "impalaSqlSessionFactory")
@EnableTransactionManagement(order = 8)
public class ImpalaDateSourceConfig {

    @Bean(name = "impalaProperties")
    @ConfigurationProperties("spring.datasource.impala")
    public Properties impalaDataSourceProperties() {
        return new Properties();
    }

    @Bean(name = "impalaDataSource")
    public DataSource impalaDataSource(@Qualifier("impalaProperties") Properties properties) throws Exception {
        return DruidDataSourceFactory.createDataSource(properties);
    }

    @Bean(name = "impalaSqlSessionFactory")
    public SqlSessionFactory testSqlSessionFactory(@Qualifier("impalaDataSource") DataSource impalaDataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(impalaDataSource);
        bean.setTypeAliasesPackage("com.jiayi.platform.judge.dto");
        org.apache.ibatis.session.Configuration configuration  = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        //configuration.setLogImpl(StdOutImpl.class);
        bean.setConfiguration(configuration);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/**/*.xml"));
        bean.setVfs(SpringBootVFS.class);
        return bean.getObject();
    }

    @Bean(name = "impalaTransactionManager")
    public DataSourceTransactionManager testTransactionManager(@Qualifier("impalaDataSource") DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
        dataSourceTransactionManager.setGlobalRollbackOnParticipationFailure(false);
        return dataSourceTransactionManager;
    }

    @Bean(name = "impalaSqlSessionTemplate")
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("impalaSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
