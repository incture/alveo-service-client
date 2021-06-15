package com.ap.menabev.dto;

import java.util.List;

import lombok.Data;

@Data
public class RemediationUser {
    private String type;
    private List<RemediationUserDto> users;
    private String message;

}
