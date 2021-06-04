package com.ap.menabev.email;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.stereotype.Service;

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

//	public static void main(String[] args) {
//		try {
//
//			//
//			// Message[] messages = fetchUnReadMessages("outlook.office365.com",
//			// ApplicationConstants.ACCPAY_EMAIL_ID,
//			// ApplicationConstants.ACCPAY_EMAIL_PASSWORD);
//			// List<Message> unReadmessageList = new ArrayList<Message>();
//			// Message unreadMeassage = null;
//			// int msgLength = messages.length;// 100
//			// System.out.println("Un Read msg Length from IMAP = " +
//			// msgLength);
//			// // Added to avoid negative messages.length - i condition ex- for
//			// // only one unread mail.
//			// for (int i = 1; i <= 5; i++) {
//			// if ((messages.length - i) >= 0) {
//			// unreadMeassage = messages[messages.length - i];
//			// unReadmessageList.add(unreadMeassage);
//			// }
//			// }
//			//
//			// for (Message message : unReadmessageList) {
//			// String contentType = message.getContentType();
//			// String messageContent = null;
//			//
//			// String attachFiles = "";
//
//			// String userName = "accpay@menabev.com";
//			// String password = "MenaBev@123";
//			// Message[] messages = fetchUnReadMessages("outlook.office365.com",
//			// userName, password);
//			// for (Message message : messages) {
//			// System.out.println(message);
//			// }
//
//		} catch (
//
//		Exception e) {
//			e.printStackTrace();
//			// TODO: handle exception
//		}
//	}

	public static void moveMessage(Message message, String inputFolderName, String outputFolderName) {

		try {

			Properties properties = new Properties();
			properties.put("mail.store.protocol", "imaps");
			Session emailSession = Session.getDefaultInstance(properties);
			Store store = emailSession.getStore();
			store.connect(ApplicationConstants.OUTLOOK_HOST, ApplicationConstants.ACCPAY_EMAIL_ID,
					ApplicationConstants.ACCPAY_EMAIL_PASSWORD);
			Folder emailFolder = store.getFolder(inputFolderName);
			Folder outputFolder = store.getFolder(outputFolderName);
			// use READ_ONLY if you don't wish the messages to be marked as read
			// after retrieving its content
			emailFolder.open(Folder.READ_WRITE);
			message.getFolder().copyMessages(new Message[] { message }, outputFolder);
			message.setFlag(Flag.DELETED, true);
			message.getFolder().expunge();

		} catch (Exception e) {
			e.printStackTrace();
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

	public static List<File> getAttachmentFromEmail(Message message) {
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
					if ("pdf".equalsIgnoreCase(fileName.substring(fileName.lastIndexOf(".") + 1))) {
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
	
	public  Message[] fetchUnReadMessagesFromSharedMailBox(String host,Integer port, String user, String password, String folderName, String flagTerm,String sharedMailId) {
		try {
			Properties properties = new Properties();
			properties.setProperty("mail.imaps.sasl.enable", "true");
			properties.setProperty("mail.imaps.sasl.authorizationid", ApplicationConstants.CSU_SHARED_MAILBOX_ID);
			Session session = Session.getInstance(properties);
			Store store = session.getStore("imaps");
//			store.connect(host, port, user, password);
			store.connect("outlook.office365.com", 993, ApplicationConstants.ACCPAY_EMAIL_ID, "MenaBev@123");
			Folder emailFolder = store.getFolder(folderName.toUpperCase());
			// use READ_ONLY if you don't wish the messages to be marked as read
			// after retrieving its content
			emailFolder.open(Folder.READ_ONLY);
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
							System.err.println(addresses[i]);
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
			e.printStackTrace();
		}
		return null;

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

}
