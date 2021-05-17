package com.ap.menabev.serviceimpl;

import java.io.File;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ap.menabev.email.Email;
import com.ap.menabev.service.EmailServices;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;
@Service
public class EmailServiceImpl implements EmailServices {
	@Autowired
	Email email;
	
	@Override
	public String sendmailTOCSU(String content, File file) {
		String message = null;
		String emailBody = null;
		try {
			if (!ServiceUtil.isEmpty(content)) {
				JSONObject json = new JSONObject(content);
				emailBody = "Hi,\nAdded by User:\n" + "1.PO Number: " + json.getString("PO_Number") + "\n2.Delivery Note: "
						+ json.getString("Delivery_Note") + "\nThanks,\nTeam AccPayable Automation";
				message = email.sendmailTOCSU(ApplicationConstants.CSU_EMAIL,
						ApplicationConstants.UPLOAD_INVOICE_TO_CSU_SUBJECT, emailBody, file);
			} else {
				emailBody="Hi,\nFind the Attachement\nThanks,\nTeam AP";
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

}