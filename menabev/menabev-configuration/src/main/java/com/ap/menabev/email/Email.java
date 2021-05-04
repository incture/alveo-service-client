package com.ap.menabev.email;

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

import org.springframework.stereotype.Service;

import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;
@Service
public class Email {

	public String sendmailTOCSU(String emailTo, String subject, String content, File file) {
		try {
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "outlook.office365.com");
			props.put("mail.smtp.port", "587");

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(ApplicationConstants.EMAIL_FROM,
							ApplicationConstants.EMAIL_FROM_PASSWORD);
				}
			});
			Message msg = new MimeMessage(session);
			//
			msg.setFrom(new InternetAddress(ApplicationConstants.EMAIL_FROM, false));

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
		} catch (Exception e) {
			return "500 "+e.getMessage();
		}
		return "200";

	}
}
