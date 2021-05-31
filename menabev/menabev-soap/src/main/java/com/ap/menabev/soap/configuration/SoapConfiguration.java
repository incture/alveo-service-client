package com.ap.menabev.soap.configuration;

import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;

@Configuration
public class SoapConfiguration {
	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller=new Jaxb2Marshaller();
		marshaller.setPackagesToScan("com.ap.menabev.soap.journalcreatebinding");
		return marshaller;
		
	}
	
//	@Bean
//	 public Wss4jSecurityInterceptor wsSecurityInterceptor() {
//	   Wss4jSecurityInterceptor wss4jSecurityInterceptor = new Wss4jSecurityInterceptor();
//	   wss4jSecurityInterceptor.setSecurementActions(WSHandlerConstants.USERNAME_TOKEN);
//	   wss4jSecurityInterceptor.setSecurementPasswordType(WSConstants.PW_TEXT);
//	   wss4jSecurityInterceptor.setSecurementUsername("Syuvraj");
//	   wss4jSecurityInterceptor.setSecurementPassword("Incture@12345");
//	   return wss4jSecurityInterceptor;
//	}

}
