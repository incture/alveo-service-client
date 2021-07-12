package com.ap.menabev;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.plaf.synth.SynthSeparatorUI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.ap.menabev.servicegrnimpl.MailConfig;



/**
 * Sends out Microsoft Adaptive Cards via E-Mail.
 */
@Configuration
public class CardMailer {

    private static final Logger LOG = LoggerFactory.getLogger(CardMailer.class);

    private static String header = "<html><head><meta http-equiv=\"Content-Type\"content=\"text/html; charset=utf-8\"><script type=\"application/adaptivecard+json\">";
    private static String footer = "</script></head><body>If you cannot see this e-mail being displayed properly, please contact your help desk.</body></html>";

    @Autowired
    MailConfig mailConfig;

    @Value("${mail.smtp.host}")
    private String smtpHost;
    @Value("${mail.smtp.port}")
    private String smtpPort;
    @Value("${mail.smtp.auth}")
    private String smtpAuth;
    @Value("${mail.smtp.starttls.enable}")
    private String smtpStartTLS;
    @Value("${mail.smtp.connectiontimeout}")
    private String smtpConnectionTimeout;
    @Value("${mail.smtp.timeout}")
    private String smtpTimeout;

    @Value("${mail.destination}")
    private String mailDestination;

    public void updateCredentialsFromDestination() throws IllegalStateException, RemoteAccessException {
        mailConfig.updateCredentialsFromDestination();
    }

    public void sendMail(List<String> recipients, String subject, String card) throws IllegalStateException, RemoteAccessException {
        String recipientsAsString = String.join(", ", recipients);
        LOG.info("Sending e-mail {} to {}...", subject, recipientsAsString);

        Properties prop = getSmtpProperties();

        Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
           
        	  
        	@Override
            protected PasswordAuthentication getPasswordAuthentication() {
   
            	 LOG.info("Sending e-mail {} to {} {} {}...password and mailUser", subject, recipientsAsString,mailConfig.getMailUser(),mailConfig.getMailPassword());
                
            	 return new PasswordAuthentication("arun.gauda@incture.com","3Pg13ec022@");
            }
        });

        LOG.info("Sending e-mail {} session...",session);
      /*  Transport transport = null;
        try {
            transport = session.getTransport("smtp");
            System.err.println("transPost "+transport);
        } catch (NoSuchProviderException e) {
            throw new IllegalStateException("Could not get SMTP transport.", e);
        }
        if (transport == null) {
            throw new IllegalStateException("Could not get SMTP transport.");
        }*/

       // try {
        	/*System.err.println("Before transPost connect ");
            transport.connect();
                         System.err.println("after TransPost connect ");;
            Message message = new MimeMessage(session);
            System.err.println("mailConfig get Mail User"+mailConfig.getMailUser());
            message.setFrom(new InternetAddress(mailConfig.getMailUser()));
            InternetAddress[] addresses = new InternetAddress[recipients.size()];
            for (int i = 0; i < recipients.size(); i++) {
                addresses[i] = new InternetAddress(recipients.get(i));
            }
            message.setRecipients(Message.RecipientType.TO, addresses);
            message.setSubject(subject);
            message.setContent(header + card + footer, "text/html; charset=utf-8");

            transport.sendMessage(message, message.getAllRecipients());*/
            
            // fix in email 
    		MimeMessage mimeMesg = new MimeMessage(session);
    		try {
    			 System.err.println("mailConfig get Mail User"+mailConfig.getMailUser());
    			 mimeMesg.setFrom(new InternetAddress(mailConfig.getMailUser()));
    	            InternetAddress[] addresses = new InternetAddress[recipients.size()];
    	            for (int i = 0; i < recipients.size(); i++) {
    	                addresses[i] = new InternetAddress(recipients.get(i));
    	            }
    	            mimeMesg.setRecipients(Message.RecipientType.TO, addresses);
    	            mimeMesg.setSubject(subject);
    	            mimeMesg.setContent(header + card + footer, "text/html; charset=utf-8");
    			Transport.send(mimeMesg);
            LOG.info("E-mail to {} successfully sent.", recipientsAsString);
        } catch (Exception e) {
            throw new RemoteAccessException("Error while sending e-mail", e);
        } 
    }

    private Properties getSmtpProperties() {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", smtpHost);
        prop.put("mail.smtp.port", smtpPort);
        prop.put("mail.smtp.auth", smtpAuth);
        prop.put("mail.smtp.starttls.enable", smtpStartTLS);
        prop.put("mail.smtp.connectiontimeout", smtpConnectionTimeout);
        prop.put("mail.smtp.timeout", smtpTimeout);
        
        LOG.error("Sending e-mail {} smtp propertise", prop); 
        
        return prop;
    }
}