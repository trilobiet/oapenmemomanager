package org.oapen.memoproject.manager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
	basePackages = "org.oapen.memoproject.manager"
)
@PropertySource("classpath:application.properties")
public class JpaConfig {}

