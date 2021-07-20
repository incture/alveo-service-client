package com.ap.menabev.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.InvoiceItemDto;
import com.ap.menabev.dto.MatchingHistoryDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.entity.MatchingHistoryDo;
import com.ap.menabev.invoice.MatchingHistoryRepository;
import com.ap.menabev.service.MatchingHistoryService;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;

@Service
public class MatchingHistoryServiceImpl implements MatchingHistoryService {

	@Autowired
	EntityManager entityManager;

	@Autowired
	MatchingHistoryRepository matchingHistoryRepository;

	@Override
	public List<MatchingHistoryDto> saveOrUpdate(List<MatchingHistoryDto> dto) {
		ModelMapper mapper = new ModelMapper();
		List<MatchingHistoryDto> response = new ArrayList<>();
		for (MatchingHistoryDto getSingleDto : dto) {
			MatchingHistoryDto dtoForResponse = new MatchingHistoryDto();
			if (ServiceUtil.isEmpty(getSingleDto.getUuid())) {
				getSingleDto.setUuid(UUID.randomUUID().toString());
				getSingleDto.setMatchScore(1);
				MatchingHistoryDo mDo = mapper.map(getSingleDto, MatchingHistoryDo.class);
				matchingHistoryRepository.save(mDo);
				dtoForResponse = mapper.map(getSingleDto, MatchingHistoryDto.class);
				dtoForResponse.setWasCreateOrUpdateSuccesfull(ApplicationConstants.CREATED_SUCCESS);
				response.add(dtoForResponse);
			} else {
				Integer matchScore = matchingHistoryRepository.getMatchingScore(getSingleDto.getUuid());
				getSingleDto.setMatchScore(matchScore + 1);
				MatchingHistoryDo mDo = mapper.map(getSingleDto, MatchingHistoryDo.class);
				matchingHistoryRepository.save(mDo);
				dtoForResponse = mapper.map(getSingleDto, MatchingHistoryDto.class);
				dtoForResponse.setWasCreateOrUpdateSuccesfull(ApplicationConstants.UPDATE_SUCCESS);
				response.add(dtoForResponse);

			}
		}
		return response;
	}

	@Override
	public List<MatchingHistoryDto> get(MatchingHistoryDto dto) {
		List<MatchingHistoryDto> response = new ArrayList<>();
		String baseQuery = "Select  uuid,vendorId,iMatNo,iUpcCode,iText,pMatNo,"
				+ "pServiceId,pUpcCode,pText,pVMN,lastMatchedBy,matchScore"
				+ " from MatchingHistoryDo";
		String query = createStringForQuery(dto);
		String finalQuery = baseQuery;
		System.out.println(query);
		if (!ServiceUtil.isEmpty(query)) {
//			int index = query.lastIndexOf(" ");
//			String sb = query.substring(0, index - 3);
			finalQuery = finalQuery + " where " + query + "matchScore = (select MAX(matchScore) from MatchingHistoryDo)";
			
		}
		
		
		System.out.println("Final Query ::::: " + finalQuery);

		Query queryForOutput = entityManager.createQuery(finalQuery);
		List<Object[]> dos = queryForOutput.getResultList();
		for(Object[] obj : dos){
			MatchingHistoryDto forResponse = new MatchingHistoryDto();
			forResponse.setUuid((String) obj[0]);
			forResponse.setVendorId((String) obj[1]);
			forResponse.setIMatNo((String) obj[2]);
			forResponse.setIUpcCode((String) obj[3]);
			forResponse.setIText((String) obj[4]);
			forResponse.setPMatNo((String) obj[5]);
			forResponse.setPServiceId((String) obj[6]);
			forResponse.setPUpcCode((String) obj[7]);
			forResponse.setPText((String) obj[8]);
			forResponse.setPVMN((String) obj[9]);
			forResponse.setLastMatchedBy((String) obj[10]);
			forResponse.setMatchScore((Integer) obj[11]);
			
			response.add(forResponse);
		}
		return response;
	}

	private String createStringForQuery(MatchingHistoryDto dto) {
		String query = "";
		if (!ServiceUtil.isEmpty(dto.getVendorId())) {
			query = query + "UPPER(vendorId) like UPPER('" + dto.getVendorId() + "') and ";
		}
		if (!ServiceUtil.isEmpty(dto.getIMatNo())) {
			query = query + "UPPER(iMatNo) like UPPER('" + dto.getIMatNo() + "') and ";
		}
		if (!ServiceUtil.isEmpty(dto.getIUpcCode())) {
			query = query + "UPPER(iUpcCode) like UPPER('" + dto.getIUpcCode() + "') and ";
		}
		if (!ServiceUtil.isEmpty(dto.getIText())) {
			query = query + "UPPER(iText) like UPPER('" + dto.getIText() + "') and ";
		}

		return query;
	}
	
	@SuppressWarnings("unused")
	private List<MatchingHistoryDto> saveToMatchingHistoryTable(InvoiceHeaderDto dto){
		List<MatchingHistoryDto> matchingHistoryDto = new ArrayList<>();
		try {
			
			if(!ServiceUtil.isEmpty(dto)){
				for(InvoiceItemDto item : dto.getInvoiceItems()){
					if(ApplicationConstants.MANUAL_MATCH.equals(item.getMatchType())){
						MatchingHistoryDto matchingHistoryObject = new MatchingHistoryDto();
						if (!ServiceUtil.isEmpty(item.getCustomerItemId())) {
							matchingHistoryObject .setCustomerItemIdVmn(item.getCustomerItemId());
						}
						if (!ServiceUtil.isEmpty(item.getItemCode())) {
							matchingHistoryObject.setIMatNo(item.getItemCode());
						}
						if (!ServiceUtil.isEmpty(item.getItemText())) {
							matchingHistoryObject.setIText(item.getItemText());
						}
						if (!ServiceUtil.isEmpty(item.getUpcCode())) {
							matchingHistoryObject.setIUpcCode(item.getUpcCode());
						}
						if (!ServiceUtil.isEmpty(item.getMatchedBy())) {
							matchingHistoryObject.setLastMatchedBy(item.getMatchedBy());
						}
						if (!ServiceUtil.isEmpty(item.getPoMatNum())) {
							matchingHistoryObject.setPMatNo(item.getPoMatNum());
						}
						if (!ServiceUtil.isEmpty(item.getPoItemText())) {
							matchingHistoryObject.setPText(item.getPoItemText());
						}
						if (!ServiceUtil.isEmpty(item.getItemCode())) {
							matchingHistoryObject.setPUpcCode(item.getArticleNum());
						}
						if (!ServiceUtil.isEmpty(item.getItemCode())) {
							matchingHistoryObject.setPVMN(item.getSetPoMaterialNum());
						}
						if (!ServiceUtil.isEmpty(dto.getVendorId())) {
							matchingHistoryObject.setVendorId(dto.getVendorId());
							
						}
						matchingHistoryDto.add(matchingHistoryObject);
						
					}
				}
				List<MatchingHistoryDto> response = saveOrUpdate(matchingHistoryDto);
				return response;
			}
		} catch (Exception e) {
			System.out.println("EXECPTION OCCUR IN MANUAL MATCH TABLE SAVE " + e.getMessage());
			return matchingHistoryDto;
		}
		return null;
	}
	
}
