package com.ap.menabev.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.SessionFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.CodesAndTextsDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.entity.CodesAndTextsDo;
import com.ap.menabev.invoice.CodesAndTextsRepository;
import com.ap.menabev.service.CodesAndTextsService;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;

@Service
public class CodesAndtextsServiceImpl implements CodesAndTextsService {

	@Autowired
	CodesAndTextsRepository codesAndTextsRepo;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
    EntityManager entityManager;
	
	@Override
	public ResponseDto saveOrUpdate(CodesAndTextsDto dto) {
	
		ResponseDto response = new ResponseDto();
		ModelMapper modelMapper = new ModelMapper();
		if(ServiceUtil.isEmpty(dto.getUuId())){
			dto.setUuId(UUID.randomUUID().toString());
		}
		try {
			CodesAndTextsDo cAndTDo = modelMapper.map(dto, CodesAndTextsDo.class);
			codesAndTextsRepo.save(cAndTDo);
			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setMessage(ApplicationConstants.SUCCESS);
			response.setStatus(
					dto.getStatusCode() + " " + ApplicationConstants.CREATED_SUCCESS);
		} catch (Exception e) {
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setMessage(ApplicationConstants.FAILURE);
			response.setStatus("Saved Failed Due to " + e.getMessage());
		}
		
		return response;
	}

	@Override
	public List<CodesAndTextsDto> get(String uuId, String statusCode, String type, String language) {
		
		ModelMapper mapper = new ModelMapper();
		List<CodesAndTextsDto> returnDto = new ArrayList<CodesAndTextsDto>();
		String baseQuery = "Select uuId,type,statusCode,language,shortText,longText from CodesAndTextsDo";
		String query = createStringForQuery(uuId,statusCode,type,language);
		String finalQuery = baseQuery;
		System.out.println(query);
		if(!ServiceUtil.isEmpty(query)){
			 int index= query.lastIndexOf(" ");
		     String sb = query.substring(0, index-3);  
			finalQuery = finalQuery + " where " + sb;
		}
		System.out.println("Final Query ::::: "+finalQuery);
		
		Query queryForOutput = entityManager.createQuery(finalQuery);
//		List<CodesAndTextsDo> dos =(List<CodesAndTextsDo>) queryForOutput.getResultList();
//        for(CodesAndTextsDo cAndTDo : dos){
//        	CodesAndTextsDto dto = new CodesAndTextsDto();
//        	dto = mapper.map(cAndTDo, CodesAndTextsDto.class);
//        	returnDto.add(dto);
//        }
		 List<Object[]> dos = queryForOutput.getResultList();
	        for(Object[] cAndTDo : dos){
	        	CodesAndTextsDto dto = new CodesAndTextsDto();
	        	dto.setUuId((String) cAndTDo[0]);
	        	dto.setType((String) cAndTDo[1]);
	        	dto.setStatusCode((String) cAndTDo[2]);
	        	dto.setLanguage((String) cAndTDo[3]);
	        	dto.setShortText((String) cAndTDo[4]);
	        	dto.setLongText((String) cAndTDo[5]);
	        	returnDto.add(dto);
	        }
		return returnDto;
	}

	private String createStringForQuery(String uuId, String statusCode, String type, String language) {
		String query = "" ;
		if(!ServiceUtil.isEmpty(uuId)){
			query = query + "uuId like '"+uuId+"' and ";
		}
		if(!ServiceUtil.isEmpty(statusCode)){
			query = query + "statusCode like '"+statusCode+"' and ";
		}
		if(!ServiceUtil.isEmpty(type)){
			query = query + "type like '"+type+"' and ";
		}
		if(!ServiceUtil.isEmpty(language)){
			query = query + "language like '"+language+"' and ";
		}
		
		return query;
	}
	

}
