package com.ap.menabev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages={"com.ap"})
public class MenaBevApplication {

	public static void main(String[] args) {
		SpringApplication.run(MenaBevApplication.class, args);

	}

}
