package com.ap.menabev.util;

public interface ApplicationConstants {
	
	
	String TRANSACTION_TYPE_INVOICE = "Invoice";
	String TRANSACTION_TYPE_CREDIT = "Credit";
	String TRANSACTION_TYPE_DEBIT = "Debit";

	// ----------------------------------------------------------//
	// SMTP Configuration Details
	String SHARED_MAIL_ID_ALLIAS = "accpay@menabev.com\\Accounts.Payable";
	String ACCPAY_EMAIL_ID = "accpay@menabev.com";
	String ACCPAY_EMAIL_PASSWORD = "MenaBev@123";
	String CSU_SHARED_MAILBOX_ID = "Accounts.Payable@menabev.com";
	String CSU_EMAIL = "Accounts.Payable@menabev.com";
	String UPLOAD_INVOICE_TO_CSU_SUBJECT = "TEST UPLOAD INVOICE";
	String SMTP_AUTH = "mail.smtp.auth";
	String SMTP_TTLS = "mail.smtp.starttls.enable";
	String MAIL_HOST = "mail.smtp.host";
	String SMTP_PORT = "mail.smtp.port";
	String TRANSPORT_PROTOCOL = "mail.transport.protocol";
	String BOUNCER_PORT = "mail.smtp.from";
	String OUTLOOK_HOST = "outlook.office365.com";
	Integer OUTLOOK_PORT = 993;
	String EMAIL_FROM = "accpay@menabev.com";
	String PROCESSED_FOLDER = "PROCESSED";
	String UNPROCESSED_FOLDER = "UNPROCESSED";
	String INBOX_FOLDER = "INBOX";
	String UNSEEN_FLAGTERM = "unseen";
	String SEEN_FLAGTERM = "seen";
	// ----------------------------------------------------------//
	// ABBYY SFTP Configuration Details
//	Integer SESSION_TIME_OUT = 10000;
//	String SOCKS_LOCATION_ID = "dip";
//	String SFTP_CHANNEL = "sftp";
//	String SFTP_USER = "SFTP";
//	String SFTP_HOST = "sftp";// Configured in cloud connector
//	String SFTP_PASSWORD = "Incture@123";
//	Integer SFTP_PORT = 8081;// Configured in cloud connector
//	String STRICT_HOST_KEY_CHECKING_KEY = "StrictHostKeyChecking";
//	String STRICT_HOST_KEY_CHECKING_VALUE = "no";
//	String ABBYY_REMOTE_INPUT_FILE_DIRECTORY = "/C/ABBYY/Input/";
//	String ABBYY_REMOTE_OUTPUT_FILE_DIRECTORY = "/C/ABBYY/Output/";

	// ----------------------------------------------------------//

	// ABBYY SFTP Configuration Details for MenaBev
	Integer SESSION_TIME_OUT = 10000;
	String SOCKS_LOCATION_ID = "DEVHEC";
	String SFTP_CHANNEL = "sftp";
	String SFTP_USER = "SYuvraj@menabev.com";
	String SFTP_HOST = "sftp";// Configured in cloud connector
	String SFTP_PASSWORD = "Menabev@159";
	Integer SFTP_PORT = 8081;// Configured in cloud connector
	String STRICT_HOST_KEY_CHECKING_KEY = "StrictHostKeyChecking";
	String STRICT_HOST_KEY_CHECKING_VALUE = "no";
	String ABBYY_REMOTE_INPUT_FILE_DIRECTORY = "";
	String ABBYY_REMOTE_OUTPUT_FILE_DIRECTORY = "";

	// ----------------------------------------------------------//

	// DEV DataBase Details
	  String DRIVER_CLASS_NAME = "com.sap.db.jdbc.Driver"; 
	  String URL = "jdbc:sap://bfddd96c-4f38-4596-917d-fa62f7c56666.hana.prod-eu20.hanacloud.ondemand.com:443?encrypt=true&validateCertificate=true";
	  String USERNAME = "MENABEVD"; 
	  String PASSWORD = "menBHDev2021";
	 
	// ----------------------------------------------------------//
	// QA DataBase Details
	/*String DRIVER_CLASS_NAME = "com.sap.db.jdbc.Driver";
	String URL = "jdbc:sap://bfddd96c-4f38-4596-917d-fa62f7c56666.hana.prod-eu20.hanacloud.ondemand.com:443?encrypt=true&validateCertificate=true";
	String USERNAME = "MENABEVQA";
	String PASSWORD = "Dv4v.6nkTn6weJh6ZSNdL7-NTR8JqwL-AVzr_H-fJI1aj78QpqmGtFggsuSLuOC4e2qCkVurhiW12D9zyv6KRVyPJqPgmWR0tVffmyDZV5Aq7K2Zx_xm-C-esUhSiLC_";*/
	// ------------------------------------------------------///

	// Service Status
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
	String REJECT_SUCCESS = "Rejected Successfully";
	String REJECT_FAILURE = "Rejection failed";
	String DELETE_SUCCESS = "Deleted Successfully";
	String DELETE_FAILURE = "Deletion failed";
	String FILTER_FOR_ISR = "InvoiceStatusReport";
	String REJECTED = "15";
	String PAYMENT_PENDING = "Pending For Approval";
	// ----------------------------------------------------------//
	// QA DataBase Details
	// String DRIVER_CLASS_NAME = "com.sap.db.jdbc.Driver";
	// String URL =
	// "jdbc:sap://bfddd96c-4f38-4596-917d-fa62f7c56666.hana.prod-eu20.hanacloud.ondemand.com:443?encrypt=true&validateCertificate=true";
	// String USERNAME = "MENABEVD";
	// String PASSWORD = "menBHDev2021";
	// ----------------------------------------------------------//
	// PROD DataBase Details
	// String DRIVER_CLASS_NAME = "com.sap.db.jdbc.Driver";
	// String URL =
	// "jdbc:sap://bfddd96c-4f38-4596-917d-fa62f7c56666.hana.prod-eu20.hanacloud.ondemand.com:443?encrypt=true&validateCertificate=true";
	// String USERNAME = "MENABEVD";
	// String PASSWORD = "menBHDev2021";

	// DocumentManagementSystem
	String DMS_TOKEN_ENDPOINT = "https://menabevdev.authentication.eu20.hana.ondemand.com/oauth/token";
	String DMS_CLIENT_ID = "sb-66065450-19f9-45c7-91ba-de9f72bb48cb!b3189|sdm-di-DocumentManagement-sdm_integration!b873";
	String DMS_CLIENT_SECRET = "DCBoqXT1Hd6gY3vOaIQ7DVie6ns=";
	String DMS_GRANT_TYPE = "client_credentials";
	String DMS_SCOPE = "generate-ads-output";
	// DEV FOLDER
	String FOLDER = "HR1vVfGxI3pTs1EpsMV2YjgVe0OujD9fX2se8ENYtj8";
	

	// QA FOLDER
	// String FOLDER = "ud3MtXve86vrngCHttSUW_KwwnMdM2KXiALY5mTLXJ4";
	// ------------------------------------------------------------------------------------//

}
