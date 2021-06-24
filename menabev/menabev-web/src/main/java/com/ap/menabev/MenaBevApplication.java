package com.ap.menabev;

import java.util.ArrayList;
import java.util.List;
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
		List<String> list= new ArrayList<>();
		list.add("a");
		list.add("e");
		String response=formInputUrl(list);
		System.out.println("response:"+response);
	}
	public static String formInputUrl(List<String> invoiceReferenceNumberList){
		StringBuilder urlForm = new StringBuilder();
		appendParamInOdataUrl(urlForm, "&$format","json" );
		appendParamInOdataUrl(urlForm, "&$filter","" );
		appendValuesInOdataUrl(urlForm,"ReferenceInvoiceNumber",invoiceReferenceNumberList);
		//appendInOdataUrl(urlForm, ")and","(" );
		urlForm.insert(0, ("/sap/opu/odata/sap/ZP2P_API_INVOICESTATUS_SRV/InvoiceStatusSet?"));
		System.err.println("url"+urlForm.toString());
		return urlForm.toString();
		}
	public static void appendValuesInOdataUrl(StringBuilder url,String key, List<String> value){
		for(int i = 0; i<value.size();i++){
			if(value.size()==1){
			url.append(key+"%20eq%20"+"%27"+value.get(i)+"%27");
			System.out.println("26 ");
			}
			else if(i==value.size()-1){
				url.append(key+"%20eq%20"+"%27"+value.get(i)+"%27");
				System.out.println("30 ");
			}else{
				url.append(key+"%20eq%20"+"%27"+value.get(i)+"%27"+"%20or%20");
				System.out.println("29 ");
				
			}
		}
	}
	public static void appendParamInOdataUrl(StringBuilder url, String key, String value) {
		url.append( key + "=" + value);
		}
		public static void appendInOdataUrl(StringBuilder url, String key, String value) {
		url.append( key + "" + value);
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
