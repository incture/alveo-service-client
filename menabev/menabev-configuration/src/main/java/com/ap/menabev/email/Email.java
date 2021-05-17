package com.ap.menabev.email;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;

@Service
public class Email {

	private static final Logger logger = LoggerFactory.getLogger(Email.class);
	

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
		} catch (Exception e) {
			return "500 " + e.getMessage();
		}
		return "200";

	}

	public Message[] fetchUnReadMessages(String host, String user, String password) {

		try {
			Properties properties = new Properties();
			properties.put("mail.store.protocol", "imaps");
			Session emailSession = Session.getDefaultInstance(properties);
			Store store = emailSession.getStore();
			store.connect(host, user, password);
			Folder emailFolder = store.getFolder("INBOX");

			// use READ_ONLY if you don't wish the messages to be marked as read
			// after retrieving its content
			emailFolder.open(Folder.READ_WRITE);
			// search for all "unseen" messages
			Flags seen = new Flags(Flags.Flag.SEEN);
			FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
			return emailFolder.search(unseenFlagTerm);
		} catch (Exception e) {
			logger.error("[ApAutomation][EmailRepository][downloadEmailAttachments][Exception] = fetchUnReadMessages"
					+ e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public void moveMessage(Message m, Folder to) throws MessagingException {
		m.getFolder().copyMessages(new Message[] { m }, to);
		m.setFlag(Flag.DELETED, true);
		m.getFolder().expunge();
	}
}
