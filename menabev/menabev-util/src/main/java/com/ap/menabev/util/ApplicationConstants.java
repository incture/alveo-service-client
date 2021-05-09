package com.ap.menabev.util;

public interface ApplicationConstants {

	String EMAIL_FROM = "dipanjan.baidya@incture.com";
	String EMAIL_FROM_PASSWORD = "Jaiburakali@123";
	// String CSU_EMAIL = "csu@menabev.com";
	String CSU_EMAIL = "prashanth.shekar@incture.com";
	String UPLOAD_INVOICE_TO_CSU_SUBJECT = "TEST UPLOAD INVOICE";

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
