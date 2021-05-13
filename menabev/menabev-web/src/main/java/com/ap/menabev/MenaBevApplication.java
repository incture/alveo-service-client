package com.ap.menabev;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.ap.menabev.util.ApplicationConstants;

@SpringBootApplication
public class MenaBevApplication {

	

	public static void main(String[] args) {
		SpringApplication.run(MenaBevApplication.class, args);

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
