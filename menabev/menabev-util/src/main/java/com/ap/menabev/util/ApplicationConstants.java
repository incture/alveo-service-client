package com.ap.menabev.util;

public interface ApplicationConstants {

	String ACCPAY_EMAIL_ID = "accpay@menabev.com";
	String ACCPAY_EMAIL_PASSWORD = "MenaBev@123";
	
	String CSU_SHARED_MAILBOX_ID = "csu@menabev.com";
	String CSU_EMAIL = "dipanjan.baidya@incture.com";
	String UPLOAD_INVOICE_TO_CSU_SUBJECT = "TEST UPLOAD INVOICE";
	
	
	//
	String SMTP_AUTH = "mail.smtp.auth";
    String SMTP_TTLS = "mail.smtp.starttls.enable";
    String MAIL_HOST = "mail.smtp.host";
    String SMTP_PORT = "mail.smtp.port";
    String TRANSPORT_PROTOCOL = "mail.transport.protocol";
    String BOUNCER_PORT = "mail.smtp.from";

	// DEV DataBase Details
	String DRIVER_CLASS_NAME = "com.sap.db.jdbc.Driver";
	String URL = "jdbc:sap://bfddd96c-4f38-4596-917d-fa62f7c56666.hana.prod-eu20.hanacloud.ondemand.com:443?encrypt=true&validateCertificate=true";
	String USERNAME = "MENABEVD";
	String PASSWORD = "menBHDev2021";
	
	//Service Status
	String SUCCESS = "Success";
	String CODE_SUCCESS = "0";
	String FAILURE = "Failure";
	String CODE_FAILURE = "1";
	String CREATED_SUCCESS = "created successfully";
	String CREATE_FAILURE = "creation failed";
	String UPDATE_SUCCESS = "updated successfully";
	String UPDATE_FAILURE = "updation failed";
	String FETCHED_SUCCESS = "fetched successfully";
	String FETCHED_FAILURE = "fetching failed";
	String REJECT_SUCCESS="Rejected Successfully";
	String REJECT_FAILURE="Rejection failed";
	String DELETE_SUCCESS = "Deleted Successfully";
	String DELETE_FAILURE = "Deletion failed";
	String FILTER_FOR_ISR = "InvoiceStatusReport";
	String REJECTED = "15";
	String PAYMENT_PENDING = "Pending For Approval";

	// QA DataBase Details
//	String DRIVER_CLASS_NAME = "com.sap.db.jdbc.Driver";
//	String URL = "jdbc:sap://bfddd96c-4f38-4596-917d-fa62f7c56666.hana.prod-eu20.hanacloud.ondemand.com:443?encrypt=true&validateCertificate=true";
//	String USERNAME = "MENABEVD";
//	String PASSWORD = "menBHDev2021";

	// PROD DataBase Details
//	String DRIVER_CLASS_NAME = "com.sap.db.jdbc.Driver";
//	String URL = "jdbc:sap://bfddd96c-4f38-4596-917d-fa62f7c56666.hana.prod-eu20.hanacloud.ondemand.com:443?encrypt=true&validateCertificate=true";
//	String USERNAME = "MENABEVD";
//	String PASSWORD = "menBHDev2021";

}
