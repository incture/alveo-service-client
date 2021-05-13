package com.ap.menabev.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ap.menabev.dto.PurchaseDocumentItemDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.entity.PurchaseDocumentItemDo;
@Service

public interface PurchaseDocumentItemService {
	ResponseDto save(PurchaseDocumentItemDto dto);

	List<PurchaseDocumentItemDto> getAll();

	ResponseDto delete(String id);

	public List<PurchaseDocumentItemDo> getPurchaseDocumentItem(String refDocNum);
}
