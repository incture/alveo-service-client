package com.ap.menabev.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
public class ToMatchOutput {
	@JsonProperty("results")
	private List<ToMatchOutputDto> results;
}
