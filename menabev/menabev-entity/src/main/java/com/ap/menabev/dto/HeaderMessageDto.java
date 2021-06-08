package com.ap.menabev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HeaderMessageDto {

	   private String msgClass;

	   private Integer messageId;

	   private String messageNumber;

	   private String messageType;

	   private String messageText;
}
