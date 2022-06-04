/*
 *     Copyright 2022-Present Ngine Apps @ http://www.ngingeapps.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ngineapps.concierge.user.management.config.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TransactionRoutingConfiguration {

  public static final String DATASOURCE_ID = "routingDataSource";
  private static final String UNIT_NAME = "concierge_user_management";
  private static final String PACKAGE_MODEL_CLASSES =
      "com.ngineapps.concierge.user.management.model";

  @Value("${app.datasource.readWrite.url}:jdbc:postgresql://localhost:5432/postgres")
  private String primaryUrl;

  @Value("${app.datasource.readWrite.maxPoolSize: 10}")
  private int primaryMaxPoolSize;

  @Value("${app.datasource.username:postgres}")
  private String username;

  @Value("${app.datasource.password: postgres}")
  private String password;

  @Value("${app.datasource.readOnly.url: jdbc:postgresql://localhost:5432/postgres}")
  private String replicaUrl;

  @Value("${app.datasource.readOnly.maxPoolSize: 10}")
  private int replicaMaxPoolSize;

  @Bean
  public DataSource readWriteDataSource() {
    PGSimpleDataSource dataSource = new PGSimpleDataSource();
    dataSource.setURL(primaryUrl);
    dataSource.setUser(username);
    dataSource.setPassword(password);
    return connectionPoolDataSource(dataSource, "readWrite-pool", primaryMaxPoolSize);
  }

  @Bean
  public DataSource readOnlyDataSource() {
    PGSimpleDataSource dataSource = new PGSimpleDataSource();
    dataSource.setURL(replicaUrl);
    dataSource.setUser(username);
    dataSource.setPassword(password);
    return connectionPoolDataSource(dataSource, "readOnly-pool", replicaMaxPoolSize);
  }

  @Primary
  @Bean(name = "routingDataSource")
  public TransactionRoutingDataSource actualDataSource() {

    TransactionRoutingDataSource routingDataSource = new TransactionRoutingDataSource();

    Map<Object, Object> dataSourceMap = new HashMap<>();
    dataSourceMap.put(DataSourceType.READ_WRITE, readWriteDataSource());
    dataSourceMap.put(DataSourceType.READ_ONLY, readOnlyDataSource());

    routingDataSource.setTargetDataSources(dataSourceMap);

    return routingDataSource;
  }

  protected HikariConfig hikariConfig(DataSource dataSource, String poolName, int maxPoolSize) {

    HikariConfig hikariConfig = new HikariConfig();

    int cpuCores = Runtime.getRuntime().availableProcessors();
    hikariConfig.setPoolName(poolName);
    /*
     * TODO Verify percentage of connections between readonly and readwrite
     * This configuration is creating same number of connections for both types per application instance
     * Verify how to manage a cluster for readonly instances to assign 100% connections as readonly
     */
    hikariConfig.setMaximumPoolSize(maxPoolSize);
    hikariConfig.setDataSource(dataSource);
    hikariConfig.setAutoCommit(false);

    return hikariConfig;
  }

  protected HikariDataSource connectionPoolDataSource(
      DataSource dataSource, String poolName, int maxPoolSize) {
    return new HikariDataSource(hikariConfig(dataSource, poolName, maxPoolSize));
  }

  @Bean
  @PersistenceContext(unitName = UNIT_NAME)
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      @Qualifier(DATASOURCE_ID) DataSource routingDataSource) {

    LocalContainerEntityManagerFactoryBean entityManagerFactory =
        new LocalContainerEntityManagerFactoryBean();
    entityManagerFactory.setPersistenceUnitName(UNIT_NAME);
    entityManagerFactory.setPackagesToScan(PACKAGE_MODEL_CLASSES);
    entityManagerFactory.setDataSource(routingDataSource);
    entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
    entityManagerFactory.afterPropertiesSet();
    return entityManagerFactory;
  }

  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }
}
