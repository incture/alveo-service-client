package com.ap.menabev;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cloud.config.java.ServiceScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

//@SpringBootApplication
//@ComponentScan(basePackages={"com.ap.menabev"})
@SpringBootApplication(scanBasePackages={"com.ap.menabev"})
@ServiceScan
//@PropertySource("classpath:application.properties")
//@ConfigurationProperties
public class MenaBevApplication {

//	@Value("${spring.datasource.username}​​​​​") 
//	static String username;
	
	public static void main(String[] args) {
		SpringApplication.run(MenaBevApplication.class, args);


	}
	
	@Bean
	@Primary
	public DataSource dataSource() {
		DataSource dataSource = DataSourceBuilder.create().type(DriverManagerDataSource.class)
				.driverClassName(com.sap.db.jdbc.Driver.class.getName())
				.url("jdbc:sap://bfddd96c-4f38-4596-917d-fa62f7c56666.hana.prod-eu20.hanacloud.ondemand.com:443?encrypt=true&validateCertificate=true")
				.username("MENABEVD")
				.password("menBHDev2021")
				.build();
		return dataSource;
	}

}
