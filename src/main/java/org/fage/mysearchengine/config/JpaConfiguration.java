package org.fage.mysearchengine.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 
 * @author Caizhfy
 * @email caizhfy@163.com
 * @createTime 2017年12月6日
 * @description JPA基础配置类
 *
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@EnableTransactionManagement(proxyTargetClass=true)
@EnableJpaRepositories(basePackages={"org.fage.mysearchengine.**.repository"})
@EntityScan(basePackages={"org.fage.mysearchengine.entity"})
public class JpaConfiguration {
	@Bean
	PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor(){
		return new  PersistenceExceptionTranslationPostProcessor();
	}
}
