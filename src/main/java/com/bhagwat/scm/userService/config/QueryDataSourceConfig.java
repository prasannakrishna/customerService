package com.bhagwat.scm.userService.config;

/*@Configuration
@EnableJpaRepositories(
        basePackages = "com.bhagwat.scm.customerService.query.repository",
        entityManagerFactoryRef = "queryEntityManagerFactory",
        transactionManagerRef = "queryTransactionManager"
)*/
public class QueryDataSourceConfig {
   /* @Bean(name = "queryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.query")
    public DataSource queryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "queryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean queryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("queryDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.bhagwat.scm.customerService.query.entity")  // <-- fixed package here
                .persistenceUnit("query")
                .build();
    }

    @Bean(name = "queryTransactionManager")
    public PlatformTransactionManager queryTransactionManager(
            @Qualifier("queryEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }*/
}
