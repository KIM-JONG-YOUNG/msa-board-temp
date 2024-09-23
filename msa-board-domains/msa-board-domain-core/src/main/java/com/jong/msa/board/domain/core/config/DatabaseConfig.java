package com.jong.msa.board.domain.core.config;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import com.jong.msa.board.common.constants.PackageNames;
import com.jong.msa.board.domain.core.converter.AbstractCodeEnumConverter;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EntityScan(
		basePackages = PackageNames.ENTITY_PACKAGE,
		basePackageClasses = AbstractCodeEnumConverter.class)
@EnableJpaRepositories(
		basePackages = PackageNames.REPOSITORY_PACKAGE)
public class DatabaseConfig {
	
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.hikari")
	HikariConfig hikariConfig() {

		return new HikariConfig();
	}

	@Bean
	DataSource dataSource(HikariConfig hikariConfig) {

		return new HikariDataSource(hikariConfig);
	}

	@Bean
	JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {

		return new JPAQueryFactory(entityManager);
	}

	@Bean
	TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {

		return new TransactionTemplate(transactionManager);
	}
	
}
