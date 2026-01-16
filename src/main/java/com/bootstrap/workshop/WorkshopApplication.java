package com.bootstrap.workshop;

import com.bootstrap.workshop.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan("com.bootstrap.workshop.config")
public class WorkshopApplication {

	public static void main(String[] args) {
		log.info("Starting Workshop Application...");
		SpringApplication.run(WorkshopApplication.class, args);
	}

	@Bean
	ApplicationRunner applicationRunner(AppConfig appConfig, Environment env) {
		return args -> {
			String[] activeProfiles = env.getActiveProfiles();
			String profiles = activeProfiles.length > 0 ? String.join(", ", activeProfiles) : "default";

			log.info("==============================================");
			log.info("Application: {} v{}", appConfig.name(), appConfig.version());
			log.info("Description: {}", appConfig.description());
			log.info("Active Profiles: {}", profiles);
			log.info("Server running on port: {}", appConfig.server().port());
			log.info("Context Path: {}", appConfig.server().contextPath());
			log.info("Health endpoint: http://localhost:{}/actuator/health", appConfig.server().port());
			log.info("Prometheus metrics: http://localhost:{}/actuator/prometheus", appConfig.server().port());
			log.info("==============================================");
			log.info("{} started successfully!", appConfig.name());
		};
	}
}
