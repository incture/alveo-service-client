package com.ap.menabev;

import java.util.concurrent.Executor;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.ap.menabev.util.ApplicationConstants;

@SpringBootApplication
@EnableAsync
public class MenaBevApplication {

	

	public static void main(String[] args) {
		SpringApplication.run(MenaBevApplication.class, args);

	}
	
	@Bean
	  public Executor taskExecutor() {
	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setCorePoolSize(2);
	    executor.setMaxPoolSize(2);
	    executor.setQueueCapacity(500);
	    executor.setThreadNamePrefix("GithubLookup-");
	    executor.initialize();
	    return executor;
	  }

	@Bean
	@Primary
	public static DataSource getDataSource() {
		DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.driverClassName(ApplicationConstants.DRIVER_CLASS_NAME);
		dataSourceBuilder.url(ApplicationConstants.URL);
		dataSourceBuilder.username(ApplicationConstants.USERNAME);
		dataSourceBuilder.password(ApplicationConstants.PASSWORD);
		return dataSourceBuilder.build();
	}

	

}
