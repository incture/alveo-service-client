package com.ap.menabev.dto;



import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MessageDto {
	private String msgId;
	private String msgNo;
	private String text;
	private String langId;
}
