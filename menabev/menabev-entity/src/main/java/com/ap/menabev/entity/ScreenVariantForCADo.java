package com.ap.menabev.entity;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.ap.menabev.dto.ScreenVariantForCAPKId;

import lombok.Data;

@Entity
@Table(name = "SCREEN_VARIANT_FOR_CA")
@IdClass(ScreenVariantForCAPKId.class)
public @Data class ScreenVariantForCADo {
	@Id
	@Column(name="USER_ID")
	 private String userId;
	
	@Id
	@Column(name="VARIANT_ID")
	 private String variantId;
	
	@Column(name="COLUMNS_DISPLAYED")
	 private String columnsDisplayed;
	
	@Column(name="COLUMNS_COUNT")
	private Integer columnsCount;
	
	@Column(name="AVAILABLE_COLUMNS")
	private String availableColumns;
	
	@Column(name="CURRENT")
	private Boolean current;
}
