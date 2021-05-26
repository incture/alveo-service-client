package com.ap.menabev.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MainTestClass {

	public static void main(String[] args) {

		 String SEQ_DATE_FORMAT = "MMddyyyy";
		
		LocalDate todaysDate = LocalDate.now();
		
		String seq = "APA-" + todaysDate.format(DateTimeFormatter.ofPattern(SEQ_DATE_FORMAT))
		+"-"+String.format("%08d", 1);
		
		System.err.println("SEQ "+seq);
		
		
	}

}
