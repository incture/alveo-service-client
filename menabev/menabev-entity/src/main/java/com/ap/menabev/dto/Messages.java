package com.ap.menabev.dto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Messages {
	private String messageClass;
	private String messageID;
	private String messageType;
	private String messageText;
}
