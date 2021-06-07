package com.ap.menabev.serviceimpl;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ap.menabev.email.Email;
import com.ap.menabev.service.EmailServices;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;

@Service
public class EmailServiceImpl implements EmailServices {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
	@Autowired
	Email email;

	@Override
	public String sendmailTOCSU(String content, File file) {
		String message = null;
		String emailBody = null;
		try {
			if (!ServiceUtil.isEmpty(content)) {
				JSONObject json = new JSONObject(content);

				if (ApplicationConstants.TRANSACTION_TYPE_INVOICE.equalsIgnoreCase(json.getString("transactionType"))) {
					emailBody = "Hi,\nAdded by User:\n" + "1.PO Number: " + json.getString("PO_Number")
							+ "\n2.Delivery Note: " + json.getString("Delivery_Note")
							+ "\nThanks,\nTeam AccPayable Automation";
					message = email.sendmailTOCSU(ApplicationConstants.CSU_EMAIL,
							ApplicationConstants.UPLOAD_INVOICE_TO_CSU_SUBJECT, emailBody, file);

				} else if (ApplicationConstants.TRANSACTION_TYPE_CREDIT
						.equalsIgnoreCase(json.getString("transactionType"))
						|| ApplicationConstants.TRANSACTION_TYPE_DEBIT
								.equalsIgnoreCase(json.getString("transactionType"))) {
					if(json.has("PO_Number")&&!ServiceUtil.isEmpty(json.getString("PO_Number"))){
						emailBody = "Hi,\nFind the Attachement for " + json.getString("transactionType").toUpperCase()
								+ " with PO NUMBER: "+json.getString("PO_Number")+".\nThanks,\nTeam AP";
						logger.error("Inside if of else");
					}else{
						emailBody = "Hi,\nFind the Attachement for " + json.getString("transactionType").toUpperCase()
								+ ".\nThanks,\nTeam AP";
						logger.error("Inside Else");
					}
					
					message = email.sendmailTOCSU(ApplicationConstants.CSU_EMAIL,
							ApplicationConstants.UPLOAD_INVOICE_TO_CSU_SUBJECT, emailBody, file);
					logger.error("Printing Message:::" + message);
				}

			} else {
				emailBody = "Hi,\nFind the Attachement\nThanks,\nTeam AP";
				message = email.sendmailTOCSU(ApplicationConstants.CSU_EMAIL,
						ApplicationConstants.UPLOAD_INVOICE_TO_CSU_SUBJECT, emailBody, file);
			}
			return message;
		} catch (Exception e) {
			e.printStackTrace();
			message = e.getMessage();
			return message;
		}
	}

	@Override
	public String readEmail() {
		try {
			email.fetchUnReadMessages(ApplicationConstants.OUTLOOK_HOST, ApplicationConstants.ACCPAY_EMAIL_ID,
					ApplicationConstants.ACCPAY_EMAIL_PASSWORD);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public String sendmailTOCSU(String emailTo, String subject, String content, File file) {
		try {

			Properties props = new Properties();
			props.put(ApplicationConstants.SMTP_AUTH, "true");
			props.put(ApplicationConstants.SMTP_TTLS, "true");
			props.put(ApplicationConstants.MAIL_HOST, "outlook.office365.com");
			props.put(ApplicationConstants.SMTP_PORT, "587");
			props.put(ApplicationConstants.TRANSPORT_PROTOCOL, "smtp");

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(ApplicationConstants.ACCPAY_EMAIL_ID,
							ApplicationConstants.ACCPAY_EMAIL_PASSWORD);
				}
			});
			Message msg = new MimeMessage(session);
			//
			msg.setFrom(new InternetAddress(ApplicationConstants.ACCPAY_EMAIL_ID, false));

			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailTo));

			msg.setSubject(subject);
			msg.setContent(content, "text/plain");
			msg.setSentDate(new Date());

			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(content, "text/plain");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			if (!ServiceUtil.isEmpty(file)) {
				MimeBodyPart attachPart = new MimeBodyPart();
				attachPart.attachFile(file);
				multipart.addBodyPart(attachPart);
			}
			msg.setContent(multipart);
			Transport.send(msg);
			return "200";
		} catch (Exception e) {
			e.printStackTrace();
			return "500 " + e.getMessage();
		}

	}

}