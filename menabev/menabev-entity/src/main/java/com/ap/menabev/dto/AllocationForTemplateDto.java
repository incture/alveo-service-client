package com.ap.menabev.dto;

import java.util.List;

import lombok.Data;

public @Data class AllocationForTemplateDto {
    private String templateName;
    private String templateId;
    private List<CostAllocationDto> costAllocationList;
}
