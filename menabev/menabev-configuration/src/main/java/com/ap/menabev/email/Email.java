package com.ap.menabev.email;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;

@Component
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

			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(ApplicationConstants.CSU_EMAIL));

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

	

	

	public Message[] fetchUnReadMessages(String host, String user, String password) {

		try {
			Properties properties = new Properties();
			properties.put("mail.store.protocol", "imaps");
			Session emailSession = Session.getDefaultInstance(properties);
			Store store = emailSession.getStore();
			store.connect(host, user, password);
			Folder emailFolder = store.getFolder("INBOX");
			Folder outputFolder = store.getFolder("Processed");
			// use READ_ONLY if you don't wish the messages to be marked as read
			// after retrieving its content
			emailFolder.open(Folder.READ_WRITE);
			// search for all "unseen" messages
			Flags seen = new Flags(Flags.Flag.SEEN);
			FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
			String EMAIL_FROM = "Dipanjan Baidya <dipanjan.baidya@incture.com>";
			// creates a search criterion
			SearchTerm searchCondition = new SearchTerm() {

				@Override
				public boolean match(Message msg) {
					Address[] addresses;
					try {
						addresses = msg.getFrom();
						for (int i = 0; i < addresses.length; i++) {
							if (addresses[i].toString().equalsIgnoreCase(EMAIL_FROM)) {
								return true;
							}
						}
					} catch (MessagingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return false;
					}
					return false;
				}
			};

			// searchConditions
			Message[] messages = emailFolder.search(unseenFlagTerm);
			Message[] filteredMessages = emailFolder.search(searchCondition, messages);

			for (Message message : filteredMessages) {
				// check for the attachment
				getAttachmentFromEmail(message);
				//
				// pass the pdfs to the abbyy server.

				// after processing the email move to the processed folder.
			}
			return filteredMessages;
		} catch (Exception e) {
			logger.error("[ApAutomation][EmailRepository][downloadEmailAttachments][Exception] = fetchUnReadMessages"
					+ e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public List<File> getAttachmentFromEmail(Message message) {
		List<File> files = new ArrayList<>();
		try {
			Multipart multiPart = (Multipart) message.getContent();
			int numberOfParts = multiPart.getCount();
			logger.error("number of parts = " + numberOfParts);
			Boolean isAttachmentFound = false;
			Boolean isAttachmentIsPdf = false;
			for (int partCount = 0; partCount < numberOfParts; partCount++) {
				MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
				if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
					isAttachmentFound = Boolean.TRUE;
					logger.error("isAttachmentFound   " + isAttachmentFound);
					String fileName = part.getFileName();
					if ("pdf".equalsIgnoreCase(fileName.substring(fileName.lastIndexOf(".") + 1))||
							"json".equalsIgnoreCase(fileName.substring(fileName.lastIndexOf(".") + 1))) {
						isAttachmentIsPdf = Boolean.TRUE;
						logger.error("isAttachmentIsPdf   " + isAttachmentIsPdf);
						File file = new File(fileName);
						part.saveFile(file);
						files.add(file);
					}
				}
			}
			return files;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
			return files;
		}

	}

//	public static void main(String[] args) throws MessagingException {
//		Email email = new Email();
//		System.out.println("For the first method:::"+System.currentTimeMillis());
//		Message[] messages = email.fetchUnReadMessagesFromSharedMailBox("outlook.office365.com", 993,
//				ApplicationConstants.SHARED_MAIL_ID_ALLIAS, ApplicationConstants.ACCPAY_EMAIL_PASSWORD,
//				ApplicationConstants.INBOX_FOLDER, ApplicationConstants.UNSEEN_FLAGTERM,
//				ApplicationConstants.EMAIL_FROM);
//		for (Message message : messages) {
//			System.err.println(message.getFrom()[0]);
////			email.moveMessage(message, "INBOX", "PROCESSED");
//		}
//		System.out.println("For the first method:::"+System.currentTimeMillis());
//		
//	}

	public void moveMessage(Message message, String inputFolderName, String outputFolderName) {

		try {

			Properties properties = new Properties();
			properties.setProperty("mail.imaps.auth.plain.disable", "true");
			Session session = Session.getInstance(properties);
			Store store = session.getStore("imaps");
			store.connect(ApplicationConstants.OUTLOOK_HOST, 993,ApplicationConstants.SHARED_MAIL_ID_ALLIAS, ApplicationConstants.ACCPAY_EMAIL_PASSWORD);
			Folder emailFolder = store.getFolder(inputFolderName);
		
			// use READ_ONLY if you don't wish the messages to be marked as read
			// after retrieving its content
			emailFolder.open(Folder.READ_ONLY);
			message.getFolder().copyMessages(new Message[] { message }, store.getFolder(outputFolderName));
			message.setFlag(Flag.DELETED, true);
//			message.getFolder().expunge();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Message[] fetchUnReadMessagesFromSharedMailBox(String host, Integer port, String user, String password,
			String folderName, String flagTerm, String emailFrom) {
		Message[] filteredMessages = null;
		try {
			logger.error("inside fetchUnReadMessagesFromSharedMailBox");
			Properties properties = new Properties();
			properties.setProperty("mail.imaps.auth.plain.disable", "true");
			properties.setProperty("mail.store.protocol", "imaps");
			Session session = Session.getInstance(properties);
			Store store = session.getStore("imaps");
			store.connect(host, port, user, password);

			Folder emailFolder = store.getFolder(folderName.toUpperCase());
			// use READ_ONLY if you don't wish the messages to be marked as read
			// after retrieving its content
			emailFolder.open(Folder.READ_WRITE);
			SearchTerm emailFromSearchTerm = new SearchTerm() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean match(Message msg) {
					Address[] addresses;
					try {
						addresses = msg.getFrom();
						for (int i = 0; i < addresses.length; i++) {
							// System.err.println(addresses[i]);
							if (addresses[i].toString().toLowerCase().contains(emailFrom.toLowerCase())
									|| addresses[i].toString().equalsIgnoreCase(emailFrom)
									) {
								
								return true;
							}
						}
					} catch (MessagingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return false;
					}
					return false;
				}
			};
			Message[] messages = null;
			if (ApplicationConstants.UNSEEN_FLAGTERM.equalsIgnoreCase(flagTerm)) {
				Flags seen = new Flags(Flags.Flag.SEEN);
				FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
				messages = emailFolder.search(unseenFlagTerm);
			} else if (ApplicationConstants.SEEN_FLAGTERM.equalsIgnoreCase(flagTerm)) {
				Flags seen = new Flags(Flags.Flag.SEEN);
				FlagTerm seenFlagTerm = new FlagTerm(seen, true);
				messages = emailFolder.search(seenFlagTerm);
			} else {
				messages = emailFolder.getMessages();
			}
			for (Message message : messages) {
				logger.error("Email Addresses::: "+message.getFrom()[0]);
			}
			// searchConditions
			if (!(ServiceUtil.isEmpty(messages) && ServiceUtil.isEmpty(emailFromSearchTerm))) {
				filteredMessages = emailFolder.search(emailFromSearchTerm, messages);
			} else {
				if (!ServiceUtil.isEmpty(messages)) {
					filteredMessages = messages;
				} else {
					filteredMessages = emailFolder.search(emailFromSearchTerm);
				}
			}
			logger.error("inside fetchUnReadMessagesFromSharedMailBox");
			
//			for (Message message : filteredMessages) {
//				
//				message.getFolder().copyMessages(new Message[] { message }, store.getFolder("PROCESSED"));
//				message.setFlag(Flag.SEEN, true);
////				message.getFolder().expunge();
//			}
			return filteredMessages;
		} catch (Exception e) {
			e.printStackTrace();
			return filteredMessages;
		}

	}

	public Message[] readEmail(String host, String user, String password, String folderName, String flagTerm) {

		try {
			Properties properties = new Properties();
			properties.put("mail.store.protocol", "imaps");
			Session emailSession = Session.getDefaultInstance(properties);
			Store store = emailSession.getStore();
			store.connect(host, user, password);
			Folder emailFolder = store.getFolder(folderName);
			// Folder outputFolder = store.getFolder("Processed");
			// use READ_ONLY if you don't wish the messages to be marked as read
			// after retrieving its content
			emailFolder.open(Folder.READ_WRITE);
			// search for all "unseen" messages
			Message[] messages = null;
			if (ApplicationConstants.UNSEEN_FLAGTERM.equalsIgnoreCase(flagTerm)) {
				Flags seen = new Flags(Flags.Flag.SEEN);
				FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
				messages = emailFolder.search(unseenFlagTerm);
			} else if (ApplicationConstants.SEEN_FLAGTERM.equalsIgnoreCase(flagTerm)) {
				Flags seen = new Flags(Flags.Flag.SEEN);
				FlagTerm seenFlagTerm = new FlagTerm(seen, true);
				messages = emailFolder.search(seenFlagTerm);
			} else {
				messages = emailFolder.getMessages();
			}
			String EMAIL_FROM = "Dipanjan Baidya <dipanjan.baidya@incture.com>";
			// creates a search criterion
			SearchTerm searchCondition = new SearchTerm() {

				@Override
				public boolean match(Message msg) {
					Address[] addresses;
					try {
						addresses = msg.getFrom();
						for (int i = 0; i < addresses.length; i++) {
							if (addresses[i].toString().equalsIgnoreCase(EMAIL_FROM)) {
								return true;
							}
						}
					} catch (MessagingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return false;
					}
					return false;
				}
			};
			Message[] filteredMessages = null;
			// searchConditions
			if (!(ServiceUtil.isEmpty(messages) && ServiceUtil.isEmpty(searchCondition))) {
				filteredMessages = emailFolder.search(searchCondition, messages);
			} else {
				if (!ServiceUtil.isEmpty(messages)) {
					filteredMessages = messages;
				} else {
					filteredMessages = emailFolder.search(searchCondition);
				}
			}
			return filteredMessages;
		} catch (Exception e) {
			logger.error("[ApAutomation][EmailRepository][downloadEmailAttachments][Exception] = fetchUnReadMessages"
					+ e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	public List<File> getAttachmentsFromEmail(String host, Integer port, String user, String password,
			String folderName, String flagTerm, String emailFrom){
		List<File> attachments = new ArrayList<>();
		Message[] filteredMessages = null;
		try {
			Properties properties = new Properties();
			properties.setProperty("mail.imaps.auth.plain.disable", "true");
			Session session = Session.getInstance(properties);
			Store store = session.getStore("imaps");
			store.connect(host, port, user, password);

			Folder emailFolder = store.getFolder(folderName.toUpperCase());
			// use READ_ONLY if you don't wish the messages to be marked as read
			// after retrieving its content
			emailFolder.open(Folder.READ_WRITE);

			SearchTerm emailFromSearchTerm = new SearchTerm() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean match(Message msg) {
					Address[] addresses;
					try {
						addresses = msg.getFrom();
						for (int i = 0; i < addresses.length; i++) {
							// System.err.println(addresses[i]);
							if (addresses[i].toString().toLowerCase().contains(emailFrom.toLowerCase())
									|| addresses[i].toString().equalsIgnoreCase(emailFrom)) {
								return true;
							}
						}
					} catch (MessagingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return false;
					}
					return false;
				}
			};
			Message[] messages = null;
			if (ApplicationConstants.UNSEEN_FLAGTERM.equalsIgnoreCase(flagTerm)) {
				Flags seen = new Flags(Flags.Flag.SEEN);
				FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
				messages = emailFolder.search(unseenFlagTerm);
			} else if (ApplicationConstants.SEEN_FLAGTERM.equalsIgnoreCase(flagTerm)) {
				Flags seen = new Flags(Flags.Flag.SEEN);
				FlagTerm seenFlagTerm = new FlagTerm(seen, true);
				messages = emailFolder.search(seenFlagTerm);
			} else {
				messages = emailFolder.getMessages();
			}
			// searchConditions
			if (!(ServiceUtil.isEmpty(messages) && ServiceUtil.isEmpty(emailFromSearchTerm))) {
				filteredMessages = emailFolder.search(emailFromSearchTerm, messages);
			} else {
				if (!ServiceUtil.isEmpty(messages)) {
					filteredMessages = messages;
				} else {
					filteredMessages = emailFolder.search(emailFromSearchTerm);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Email.getAttachmentsFromEmail():::Error:"+e.getMessage());
			// TODO: handle exception
		}finally {
			
		}
		return attachments;
	}
	public String getEmail(){
		try {
			
			Properties props = new Properties();
			props.setProperty("mail.store.protocol", "imaps");
			props.setProperty("mail.imaps.sasl.enable", "true");
			props.setProperty("mail.imaps.sasl.authorizationid", "accounts.payble@menabev.com");
			Session session = Session.getInstance(props);
			Store store = session.getStore("imaps");
			store.connect("outlook.office365.com", 993, "accpay@menabev.com", "MenaBev@123");
			return "ok";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "error";
		}
	}
}
