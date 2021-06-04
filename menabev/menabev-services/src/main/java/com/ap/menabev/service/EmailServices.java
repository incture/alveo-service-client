package com.ap.menabev.service;

import java.io.File;
public interface EmailServices {
	String sendmailTOCSU(String content, File file);

	String readEmail();
}
