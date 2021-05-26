package com.ap.menabev.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {

	public static String dateFormatForTemplateId = LocalDate.now(ZoneId.systemDefault())
			.format(DateTimeFormatter.ofPattern("yyMMd"));

	public String dateFormatForECC = LocalDateTime.now(ZoneId.of("GMT+08:00"))
			.format(DateTimeFormatter.ofPattern("d.MM.yyyy HH:mm:ss"));

	// yyyyMMddHHmmss.SSS
	public static String dateFormatForWorkflowTask = LocalDateTime.now(ZoneId.of("GMT+05:30"))
			.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSS"));

	public String dateFormatForWorkflowTask(String date) {
		return Instant.ofEpochMilli(Long.parseLong(date)).atZone(ZoneId.of("GMT+00:00")).toLocalDateTime()
				.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSS"));
	}

//	public static void main(String[] args) {
//		String d = new String("1599013800000");
//
//		// DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSS").
////		System.out.println(Instant.ofEpochMilli(Long.parseLong(d)).atZone(ZoneId.of("GMT+00:00")).toLocalDateTime()
////				.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSS")));
//
//		System.out.println(new DateUtils().dateFormatForWorkflowTask(d));
//
//	}

	//public static final String SEQ_DATE_FORMAT = "yyMMdd";
	public static final String SEQ_DATE_FORMAT = "MMddyyyy";

	public static long getCurrentTimeInEPOCHSec() {

		long millis = System.currentTimeMillis() / 1000;

		return millis;
	}
}
