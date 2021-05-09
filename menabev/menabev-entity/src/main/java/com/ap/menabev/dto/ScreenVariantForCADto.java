package com.ap.menabev.dto;



import lombok.Data;

public @Data class ScreenVariantForCADto {
 private String userId;
 private String variantId;
 private String columnsDisplayed;
 private Integer columnsCount;
 private String availableColumns;
 private Boolean current;
}


 