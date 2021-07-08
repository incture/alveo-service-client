package com.ap.menabev.servicegrnimpl;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.RemoteAccessException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Configuration
public class MailConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MailConfig.class);

    @Autowired
    private VcapConfig vcapConfig;

    @Value("${mail.destination}")
    private String mailDestination;

    private String mailUser;
    private String mailPassword;

    public void updateCredentialsFromDestination() throws IllegalStateException, RemoteAccessException {
        String accessToken = UaaUtils.getDestinationAccessToken(vcapConfig);

        String destination = getDestination(accessToken);

        ObjectMapper mapper = new ObjectMapper();
        try {
            ObjectNode destinationNode = (ObjectNode) mapper.readTree(destination);
            //this.mailUser = destinationNode.path("mail.user").asText();
           // this.mailPassword = destinationNode.path("mail.password").asText();
            this.mailUser = "arun.gauda@incture.com";
            this.mailPassword = "3Pg13ec022@";
        } catch (IOException e) {
            throw new RemoteAccessException("Error while processing destination " + mailDestination, e);
        }
    }

    private String getDestination(String accessToken) throws IllegalStateException, RemoteAccessException {
        String destination = "{}";
        String destinationAPI = vcapConfig.getDestinationRestUrl();
        String destinationURL = destinationAPI + "/destination-configuration/v1/subaccountDestinations/" + mailDestination;

        LOG.info("Destination endpoint for destination {}: {}",  mailDestination, destinationURL);

        HttpGet httpGet = new HttpGet(destinationURL);
        httpGet.setHeader("Authorization", "Bearer " + accessToken);

        try (CloseableHttpClient httpclient = HttpClients.createDefault();
                CloseableHttpResponse response = httpclient.execute(httpGet);
                InputStream content = HttpUtils.getContent(response, "{}");
                Scanner scanner = new Scanner(content, "utf-8")) {
            destination = scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
            throw new RemoteAccessException("Error while retrieving destination " + mailDestination, e);
        }
        LOG.info("Destination endpoint for destination {}: {destination info :}", destination);
        return destination;
    }

    public String getMailUser() {
        return mailUser;
    }

    public String getMailPassword() {
        return mailPassword;
    }

}
