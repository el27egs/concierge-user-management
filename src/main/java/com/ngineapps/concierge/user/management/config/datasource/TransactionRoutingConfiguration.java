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
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = "com.ngineapps.concierge.user.management")
public class TransactionRoutingConfiguration {

	@Value("${app.datasource.readWrite.url}")
	private String primaryUrl;

	@Value("${app.datasource.username}")
	private String username;

	@Value("${app.datasource.password}")
	private String password;

	@Value("${app.datasource.readOnly.url}")
	private String replicaUrl;

	@Bean
	public DataSource readWriteDataSource() {
		PGSimpleDataSource dataSource = new PGSimpleDataSource();
		dataSource.setURL(primaryUrl);
		dataSource.setUser(username);
		dataSource.setPassword(password);
		return connectionPoolDataSource(dataSource, "Hikary-readWrite-pool");
	}

	@Bean
	public DataSource readOnlyDataSource() {
		PGSimpleDataSource dataSource = new PGSimpleDataSource();
		dataSource.setURL(replicaUrl);
		dataSource.setUser(username);
		dataSource.setPassword(password);
		return connectionPoolDataSource(dataSource, "Hikary-readOnly-pool");
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

	protected HikariConfig hikariConfig(DataSource dataSource, String poolName) {

		HikariConfig hikariConfig = new HikariConfig();

		int cpuCores = Runtime.getRuntime().availableProcessors();
		hikariConfig.setPoolName(poolName);
		hikariConfig.setMaximumPoolSize(cpuCores * 4);
		hikariConfig.setDataSource(dataSource);
		hikariConfig.setAutoCommit(false);

		return hikariConfig;
	}

	protected HikariDataSource connectionPoolDataSource(DataSource dataSource, String poolName) {
		return new HikariDataSource(hikariConfig(dataSource, poolName));
	}

	@Bean
	@PersistenceContext(unitName = "concierge_user_management")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			@Qualifier("routingDataSource") DataSource routingDataSource) {

		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactory.setPersistenceUnitName("concierge_user_management");
		entityManagerFactory.setPackagesToScan("com.ngineapps.concierge.user.management.model");
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
