package com.ap.menabev.util;

import java.util.Arrays;
import java.util.List;

public interface ApplicationConstants {
	
	String APMAILBOX = "APMAILBOX";
	String APSCANNINGTEAM = "APSCANNINGTEAM";
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
	//String EMAIL_FROM = "accpay@menabev.com";
	String PROCESSED_FOLDER = "PROCESSED";
	String UNPROCESSED_FOLDER = "UNPROCESSED";
	String INBOX_FOLDER = "INBOX";
	String UNSEEN_FLAGTERM = "unseen";
	String SEEN_FLAGTERM = "seen";
	List<String> EMAIL_FROM = Arrays.asList("accpay@menabev.com","dipanjan.baidya@incture.com","anushri.br@incture.com");
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
//	String DRIVER_CLASS_NAME = "com.sap.db.jdbc.Driver";
//	String URL = "jdbc:sap://bfddd96c-4f38-4596-917d-fa62f7c56666.hana.prod-eu20.hanacloud.ondemand.com:443?encrypt=true&validateCertificate=true";
//	String USERNAME = "MENABEVQA";
//	String PASSWORD = "Dv4v.6nkTn6weJh6ZSNdL7-NTR8JqwL-AVzr_H-fJI1aj78QpqmGtFggsuSLuOC4e2qCkVurhiW12D9zyv6KRVyPJqPgmWR0tVffmyDZV5Aq7K2Zx_xm-C-esUhSiLC_";
	// ------------------------------------------------------///

	// Service Status
	String SUCCESS = "Success";
	String CODE_SUCCESS = "0";
	String FAILURE = "Failure";
	String CODE_FAILURE = "1";
	String CREATED_SUCCESS = "created successfully";
	String CREATE_FAILURE = "creation failed";
	String UPDATE_SUCCESS = "Updated successfully";
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
	//---------------------------------------------------//
	//Submit Constants 
	String ACCOUNTANT_SUBMIT_FOR_REMEDIATION = "ASR";
	String ACCOUNTANT_SUBMIT_FOR_APPROVAL = "ASA";
	String ACCOUNTANT_REJECT = "AR";
	String PROCESS_LEAD_APPROVAL = "PA";
	String PROCESS_LEAD_REJECTION = "PR";
	String PROCESS_LEAD_RESEND = "PRS";
	String BUYER_APPROVE = "A";
	String BUYER_REJECT ="R";
	String NEW_INVOICE = "0";
	String DRAFT_INVOICE = "1";
	String OPEN_INVOICE = "2";
	String DUPLICATE_INVOICE = "3";
	String PO_MISSING_OR_INVALID ="4";  
	String NO_GRN = "5";
	String PARTIAL_GRN = "6";
	String UOM_MISMATCH = "7";
	String ITEM_MISMATCH = "8";
	String QTY_MISMATCH = "9";
	String PRICE_MISMATCH = "10";
	String PRICE_OR_QTY_MISMATCH  = "11";
	String BALANCE_MISMATCH = "12";
	String READY_TO_POST = "17";
	String GRN_PASSED = "19";
	String DUPLICATE_PASSED = "20";
	String SAP_POSTED = "13";
	String PAID ="14";
	String UNPAID ="13";
	String DUPLICATE = "A";
    String NOT_AN_INVOICE = "B";
    String PRICE_IS_NOT_RIGHT = "C";
    String QTY_IS_NOT_RIGHT = "D";
    String VAT_CODE_MISMATCH = "E";
    String MANUAL_MATCH = "MAN";
    String AUTO_MATCH = "AUTO";
    String THREE_WAY_MATCH_SUCCESS = "18";
    String PENDING_APPROVAL = "16";
    String DUPLICATE_CHECK_PASSED = "21";
    String QUANTITY_HIGH_MSG_NUMBER = "504";
	String QUANTITY_LOW_MSG_NUMBER="";
	String PRICE_HIGH_MSG_NUMBER = "082";
	String PRICE_LOW_MSG_NUMBER = "084";
	
	
	public static final String MENABEV_ODATA_DESTINATION_NAME = "";
	public static final String TASK_COMPLETED = "COMPLETED";
	public static final String INVOICE_SEQUENCE = "APA";
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
	String LIFE_CYCLE_STATUS_OPEN = "Open";
	String CHANEL_TYPE_EMAIL = "Email";
	
	

	// QA FOLDER
    //String FOLDER = "ud3MtXve86vrngCHttSUW_KwwnMdM2KXiALY5mTLXJ4";
	// ------------------------------------------------------------------------------------//
	 // worklfow service key
	//Dev
   public static final String WORKFLOW_TOKEN_URL = "https://menabevdev.authentication.eu20.hana.ondemand.com/oauth/token?grant_type=client_credentials";
	public static final String WORKFLOW_CLIENT_ID = "sb-clone-a3d7240c-7019-465b-b91b-07ad27fee8c0!b3189|workflow!b246";
	public static final String WORKFLOW_CLIENT_SECRETE = "63f7cf76-0a32-4675-933b-776e34de0ef2$Ldp9wLW-VlhVrRec1w-HRfMqTORzQC0RD_UIoBnQiGY=";
  // QA 
//  public static final String WORKFLOW_TOKEN_URL = "https://menabev-p2pautomation-test.authentication.eu20.hana.ondemand.com/oauth/token?grant_type=client_credentials";
//	public static final String WORKFLOW_CLIENT_ID = "sb-clone-438954f0-66df-475d-9856-c006fbcc3fe2!b3073|workflow!b246";
//	public static final String WORKFLOW_CLIENT_SECRETE = "b6f8a88e-ce98-45b6-8b3e-17d4ccdce3ec$07VY759NN4MiaStbPQW77HXos2rg2THB9HOBP3iiFnE=";
	
	public static final String WORKFLOW_REST_BASE_URL = "https://api.workflow-sap.cfapps.eu20.hana.ondemand.com/workflow-service/rest";
   // ------------------------------------------------------------------------------------------>
	// Rule Service Key 
	//Dev 
	   public static final String OAUTH_TOKEN_URL = "https://menabevdev.authentication.eu20.hana.ondemand.com/oauth/token?grant_type=client_credentials";
	    public static final String RULE_CLIENT_ID = "sb-clone-0ff4e68a-828d-4ca0-9970-e312fe2e85f9!b3189|bpmrulebroker!b335";
	    public static final String RULE_CLIENT_SECRETE = "042d1aa1-6313-4c5a-a120-c6c648f57e8b$jBrW3wXl7yz03NRidVv3b160lk4iU_6U-g6O0tPM5HQ=";
	
	// QA
//	public static final String OAUTH_TOKEN_URL = "https://menabev-p2pautomation-test.authentication.eu20.hana.ondemand.com/oauth/token?grant_type=client_credentials";
//    public static final String RULE_CLIENT_ID = "sb-clone-041b54be-58fd-47dd-bd5c-5db0310f77e0!b3073|bpmrulebroker!b335";
//    public static final String RULE_CLIENT_SECRETE = "8d3e3a7d-a047-4686-bc57-a28cf27f0049$iQ3D2ivwPh6wcJjvmJWf1-wJvXL62Ci1k4YOTVhF2Ts=";
	
	
	
	
	
	//Connectivity Service Key Details For DEV
		public static final String CONECTIVITY_CLIENT_ID = "sb-cloneb41bf10568ca4499840711bb8a0f2de4!b3189|connectivity!b5";
		public static final String CONECTIVITY_CLIENT_SECRET = "d56e99cf-76a5-4751-b16b-5e912f1483dc$iVWHjYhERnR-9oYc_ffRYWShcnGbdSdLQ4DOnPcpc5I=";
		public static final String CONECTIVITY_TOKEN_URL = "https://menabevdev.authentication.eu20.hana.ondemand.com/oauth/token?grant_type=client_credentials";
	 
	//Connectivity Service Key Details For QA
//	    public static final String CONECTIVITY_CLIENT_ID = "sb-clone38f786be563c4447b1ac03fe5831a53f!b3073|connectivity!b5";
//		public static final String CONECTIVITY_CLIENT_SECRET = "9c5a2d59-abb3-4c8f-bdba-c3b0222ceb25$iBxUjgTsDHnBRBuByPBR7qfSnY77pLPYV-_QZkhzC5I=";
//		public static final String CONECTIVITY_TOKEN_URL = "https://menabev-p2pautomation-test.authentication.eu20.hana.ondemand.com/oauth/token?grant_type=client_credentials";
	
		
//----------------------------------------------------------------------------------------->	
	//Destination Service Key details for DEV
		public static final String DESTINATION_CLIENT_ID = "sb-clone4768d4738f4b49498258b8a01b20230a!b3189|destination-xsappname!b2";
		public static final String DESTINATION_CLIENT_SECRET = "2af4f4c4-7265-4d95-b544-01e917937a1e$HlHDn__C2aLbv2PqTcyq251kX4P9QZmZDShfUEFw8NQ=";
		public static final String DESTINATION_TOKEN_URL = "https://menabevdev.authentication.eu20.hana.ondemand.com/oauth/token?grant_type=client_credentials";
		public static final String DESTINATION_BASE_URL = "https://destination-configuration.cfapps.eu20.hana.ondemand.com/destination-configuration/v1/destinations/";
	
		//Destination Service Key details for QA
//	    public static final String DESTINATION_CLIENT_ID = "sb-clone4e56aaf015b44e7e9abcbc4fab151c7d!b3073|destination-xsappname!b2";
//		public static final String DESTINATION_CLIENT_SECRET = "524ca357-c878-44f9-80ef-de7ec2fd7a6f$6AARTEnbSvVaq9lZREb73HxkJdFem3wMWvSReCQByLA=";
//		public static final String DESTINATION_TOKEN_URL = "https://menabev-p2pautomation-test.authentication.eu20.hana.ondemand.com/oauth/token?grant_type=client_credentials";
//		public static final String DESTINATION_BASE_URL = "https://destination-configuration.cfapps.eu20.hana.ondemand.com/destination-configuration/v1/destinations/";
		
		//Destination Service Key details for PROD
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
