package com.ap.menabev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
public class ThreeWayMatchOdataDto {
	@JsonProperty("d")
	private ThreeWayMatchingRootNode d;
}
