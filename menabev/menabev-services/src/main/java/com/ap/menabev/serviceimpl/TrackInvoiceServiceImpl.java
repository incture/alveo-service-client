package com.ap.menabev.serviceimpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.ListUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.OdataOutPutPayload;
import com.ap.menabev.dto.OdataResponseDto;
import com.ap.menabev.dto.OdataResultObject;
import com.ap.menabev.dto.OdataTrackInvoiceObject;
import com.ap.menabev.dto.OdataTrackInvoiceOutputPayload;
import com.ap.menabev.dto.OdataTrackInvoiceResponseDto;
import com.ap.menabev.dto.RemediationUser;
import com.ap.menabev.dto.RemediationUserDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.TrackInvoiceInputDto;
import com.ap.menabev.dto.TrackInvoiceOdataOutputResponse;
import com.ap.menabev.dto.TrackInvoiceOutputDto;
import com.ap.menabev.dto.TrackInvoiceOutputPayload;
import com.ap.menabev.entity.InvoiceHeaderDo;
import com.ap.menabev.invoice.InvoiceHeaderRepoFilter;
import com.ap.menabev.invoice.InvoiceHeaderRepository;
import com.ap.menabev.service.PoSearchApiService;
import com.ap.menabev.service.TrackInvoiceService;
import com.ap.menabev.util.OdataHelperClass;
import com.ap.menabev.util.ServiceUtil;
import com.google.gson.Gson;
 
@Service
public class TrackInvoiceServiceImpl implements TrackInvoiceService {

	private static final Logger logger = LoggerFactory.getLogger(TrackInvoiceServiceImpl.class);

	@Autowired
	InvoiceHeaderRepository invoiceHeaderRepository;

	@Autowired
	OdataHelperClass odataHelperClass;

	@Autowired
	InvoiceHeaderRepoFilter invoiceHeaderRepoFilter;
	
	@Autowired
	PoSearchApiServiceImpl poSearchApiServiceImpl;

	@SuppressWarnings("static-access")
	public ResponseEntity<?> fetchTrackInvoice(TrackInvoiceInputDto trackInvoiceInputDto) {
		ResponseDto responseDto=new ResponseDto();
		List<InvoiceHeaderDo> headerList= filterInvoicesMultiple(trackInvoiceInputDto);
		System.err.println("headerList :"+headerList);
		ModelMapper modelMapper = new ModelMapper();
		List<InvoiceHeaderDto> sapPostedList=new ArrayList<>();
		List<InvoiceHeaderDto> pendingApprovalList=new ArrayList<>();
		List<InvoiceHeaderDto> rejectedList=new ArrayList<>();
		List<String> invoiceReferenceNumberList=new ArrayList<>();
		List<InvoiceHeaderDto> finalpayload=new ArrayList<>();

		if(!ServiceUtil.isEmpty(headerList))
		{
			System.err.println("headerList invoiceNumber:"+headerList.get(0).getInvoice_ref_number());
		for (InvoiceHeaderDo invoiceHeaderDo:headerList){
			if(invoiceHeaderDo.getInvoiceStatus()=="13" && invoiceHeaderDo.getInvoiceStatus()=="14" )
			{
				System.err.println("headerList sapPostedDto:"+invoiceHeaderDo.getInvoiceStatus());

				InvoiceHeaderDto sapPostedDto=modelMapper.map(invoiceHeaderDo, InvoiceHeaderDto.class);
				System.err.println("headerList sapPostedDto:"+sapPostedDto);

				sapPostedList.add(sapPostedDto);
			}
			else if(invoiceHeaderDo.getInvoiceStatus()=="15")
			{
				System.err.println("headerList rejectedDto:"+invoiceHeaderDo.getInvoiceStatus());

				InvoiceHeaderDto sapRejectedDto=modelMapper.map(invoiceHeaderDo, InvoiceHeaderDto.class);
				System.err.println("headerList rejectedDto:"+sapRejectedDto);

				rejectedList.add(sapRejectedDto);
			}
			else
			{
				System.err.println("headerList pendingApprovalDto:"+invoiceHeaderDo.getInvoiceStatus());

				InvoiceHeaderDto sapPendingApprovaldDto=modelMapper.map(invoiceHeaderDo, InvoiceHeaderDto.class);
				invoiceHeaderDo.setInvoiceStatus("16");
				invoiceHeaderDo.setInvoiceStatusText("Pending Approval");
				System.err.println("headerList pendingApprovalDto:"+invoiceHeaderDo);

				pendingApprovalList.add(sapPendingApprovaldDto);
			}
		 }
		for(InvoiceHeaderDto headerDo:sapPostedList ){
				invoiceReferenceNumberList.add(headerDo.getExtInvNum());
				System.err.println("invoiceReferenceNumberList:"+invoiceReferenceNumberList);
		}
				//ResponseEntity<?> odataResponse=odataHelperClass.consumingOdataServiceForTrackInvoice("/sap/opu/odata/sap/ZP2P_API_INVOICESTATUS_SRV/InvoiceStatusSet", invoiceReferenceNumberList.toString(), "GET", odataHelperClass.getDestination("SD4_DEST"));
		}
		if(!ServiceUtil.isEmpty(invoiceReferenceNumberList)){
		try{
		Map<String, Object> map = odataHelperClass.getDestination("SD4_DEST");
		String endPointurl = formInputUrl(invoiceReferenceNumberList);
		System.err.println("endPointurl:"+endPointurl);
		ResponseEntity<?> odataResponse=odataHelperClass.consumingOdataService(endPointurl,null, "GET", map);
		 if(odataResponse.getStatusCodeValue()==200){
             String jsonOutputString = (String) odataResponse.getBody();
             System.err.println("ECCResponse "+jsonOutputString);
          // convert OuputResponse  
             TrackInvoiceOdataOutputResponse  trackInvoiceOdataOutputResponse =  formOutPutSuccessResponse(jsonOutputString);
	             System.err.println("convertedResponse "+trackInvoiceOdataOutputResponse);
	           if(!ServiceUtil.isEmpty( trackInvoiceOdataOutputResponse.getUsers())){
	        	   for(OdataTrackInvoiceObject odataTrackInvoiceObject:trackInvoiceOdataOutputResponse.getUsers())
	        	   {
	        		   if(!ServiceUtil.isEmpty(odataTrackInvoiceObject.getClearingDate())){
	        			   
	        			   for(InvoiceHeaderDto invoiceHeaderDto:sapPostedList){
	        				   invoiceHeaderDto.setInvoiceStatus("16");
	        				   invoiceHeaderDto.setInvoiceStatusText("Paid");
	        				   invoiceHeaderDto.setClearingDate(odataTrackInvoiceObject.getClearingDate());
	        				   invoiceHeaderDto.setPaymentReference(odataTrackInvoiceObject.getPaymentStatus());
	        				   sapPostedList.add(invoiceHeaderDto);
	        			   }
	        	   }
	           }
	        	   List<InvoiceHeaderDto> newList = Stream.of(sapPostedList, pendingApprovalList, rejectedList)                          .flatMap(Collection::stream)
                           .collect(Collectors.toList());  
	        	   System.err.println("newList:"+newList);
	        	   TrackInvoiceOutputPayload trackInvoiceOutputPayload=new TrackInvoiceOutputPayload();
	        	   trackInvoiceOutputPayload.setUsers(newList);
	        	   trackInvoiceOutputPayload.setType("Success");
	        	   trackInvoiceOutputPayload.setMessage("Success");
	             return new ResponseEntity<TrackInvoiceOutputPayload>(trackInvoiceOutputPayload,HttpStatus.OK);
          }else {
           	  String jsonOutputStr = (String) odataResponse.getBody();
           	TrackInvoiceOutputPayload  errorMessage =  formOutPutFailureResponse(jsonOutputString);	  
           	  System.err.println("convertedRrrorResponse "+odataResponse);
  		return new ResponseEntity<TrackInvoiceOutputPayload>(errorMessage,HttpStatus.BAD_REQUEST);
          }
		 }
		}
		 catch(Exception e)
		 {
			 e.printStackTrace();
			 return null;
		 }
		}
		else{
			TrackInvoiceOutputPayload trackInvoiceOutputPayload=new TrackInvoiceOutputPayload();
			trackInvoiceOutputPayload.setMessage("Refrerence number is not found for the specipic vendor number");
			trackInvoiceOutputPayload.setType("ERROR");
			trackInvoiceOutputPayload.setUsers(Collections.emptyList());
			System.err.println("Refrerence number is not found for the specipic vendor number");
	  		return new ResponseEntity<TrackInvoiceOutputPayload>(trackInvoiceOutputPayload,HttpStatus.BAD_REQUEST);
		}
		return null;
	}
	public TrackInvoiceOdataOutputResponse formOutPutSuccessResponse(String jsonOutputString)
	{
		TrackInvoiceOdataOutputResponse trackInvoiceOdataOutputResponse = new TrackInvoiceOdataOutputResponse();
		trackInvoiceOdataOutputResponse.setType("TEST_SUCCESS");
		trackInvoiceOdataOutputResponse.setMessage("Success");
         List<OdataTrackInvoiceObject> trackInvoiceOutputDtoList = new ArrayList<OdataTrackInvoiceObject>();
         OdataTrackInvoiceResponseDto  taskDto =     convertStringToJsonForOdataSuccess(jsonOutputString);
        OdataTrackInvoiceOutputPayload    outResp =  taskDto.getD();
                 List<OdataTrackInvoiceObject> resultList = outResp.getResults();
                 resultList.stream().forEach(r->{
              	 trackInvoiceOutputDtoList.add(r);
                 });
                 trackInvoiceOdataOutputResponse.setUsers(trackInvoiceOutputDtoList); 
                 return trackInvoiceOdataOutputResponse;
	}
	//Faiure
			public TrackInvoiceOutputPayload formOutPutFailureResponse(String jsonOutputString)
			{
				TrackInvoiceOutputPayload errorMessage = new TrackInvoiceOutputPayload();
		    	  OdataErrorResponseDto response =  convertStringToJsonForOdataFailure(jsonOutputString);
				errorMessage.setType("TEST_ERROR");
		    	  errorMessage.setMessage(response.getError().getMessage().getValue());
		        
		                 errorMessage.setUsers(Collections.emptyList()); 
		                 return errorMessage;
			  
			}
			
			// get odata success body
			public OdataTrackInvoiceResponseDto convertStringToJsonForOdataSuccess(String json){
				OdataTrackInvoiceResponseDto  taskDto = new Gson().fromJson(json.toString(),
						OdataTrackInvoiceResponseDto.class);
				return taskDto;
			}
			// get odata Failure  body 
			public OdataErrorResponseDto convertStringToJsonForOdataFailure(String json){
				OdataErrorResponseDto response = new Gson().fromJson(json.toString(),
						OdataErrorResponseDto.class);
				return response;
			}
	public String formInputUrl(List<String> invoiceReferenceNumberList){
		StringBuilder urlForm = new StringBuilder();
		appendParamInOdataUrl(urlForm, "&$format","json" );
		appendParamInOdataUrl(urlForm, "&$filter","" );
		appendValuesInOdataUrl(urlForm,"ReferenceInvoiceNumber",invoiceReferenceNumberList);
		//appendInOdataUrl(urlForm, ")and","(" );
		urlForm.insert(0, ("/sap/opu/odata/sap/ZP2P_API_INVOICESTATUS_SRV/InvoiceStatusSet?"));
		System.err.println("url"+urlForm.toString());
		return urlForm.toString();
		}
	public static void appendValuesInOdataUrl(StringBuilder url,String key, List<String> value){
		for(int i = 0; i<value.size();i++){
			if(value.size()==1){
			url.append(key+"%20eq%20"+"%27"+value.get(i)+"%27");
			System.out.println("26 ");
			}
			else if(i==value.size()-1){
				url.append(key+"%20eq%20"+"%27"+value.get(i)+"%27");
				System.out.println("30 ");
			}else{
				url.append(key+"%20eq%20"+"%27"+value.get(i)+"%27"+"%20or%20");
				System.out.println("29 ");
				
			}
		}
	}
	public static void appendParamInOdataUrl(StringBuilder url, String key, String value) {
		url.append( key + "=" + value);
		}
		public static void appendInOdataUrl(StringBuilder url, String key, String value) {
		url.append( key + "" + value);
		}
		
	private List<InvoiceHeaderDo> filterInvoicesMultiple(TrackInvoiceInputDto dto) {
		List<InvoiceHeaderDo> invoiceOrderList = null;
		StringBuffer query = new StringBuffer();
		Map<String, String> filterQueryMap = new HashMap<String, String>();

		query.append("SELECT * FROM INVOICE_HEADER RR WHERE");
		if (dto.getRequestId() != null && !dto.getRequestId().isEmpty()) {
			StringBuffer rqstId = new StringBuffer();
			/*
			 * for (int i = 0; i < requestId.size(); i++) { if (i <
			 * dto.getRequestId().size() - 1) { rqstId.append("'" +
			 * requestId.get(i) + "'" + ","); } else { rqstId.append("'" +
			 * requestId.get(i) + "'"); } }
			 */
			filterQueryMap.put(" RR.REQUEST_ID IN", "('"  + dto.getRequestId() + "')");
		}
		if (dto.getCompanyCode() != null && !dto.getCompanyCode().isEmpty()) {
			StringBuffer rqstId = new StringBuffer();
			/*
			 * for (int i = 0; i < requestId.size(); i++) { if (i <
			 * dto.getRequestId().size() - 1) { rqstId.append("'" +
			 * requestId.get(i) + "'" + ","); } else { rqstId.append("'" +
			 * requestId.get(i) + "'"); } }
			 */
			filterQueryMap.put(" RR.COMPANY_CODE IN", "('" + dto.getCompanyCode() + "')");
		}
		if (dto.getVendorId() != null && !dto.getVendorId().isEmpty()) {
			StringBuffer rqstId = new StringBuffer();
			for (int i = 0; i < dto.getVendorId().size(); i++) {
				if (i < dto.getVendorId().size() - 1) {
					rqstId.append("'" + dto.getVendorId().get(i) + "'" + ",");
				} else {
					rqstId.append("'" + dto.getVendorId().get(i) + "'");
				}
			}
			filterQueryMap.put(" RR.VENDOR_ID IN", "(" + rqstId + ")");
		}
		if (dto.getDueDateFrom() != 0 && dto.getDueDateTo() != 0) {
			filterQueryMap.put(" RR.DUE_DATE BETWEEN ", +dto.getDueDateFrom() + " AND " + dto.getDueDateTo());
			// is empty
		}
		if (dto.getDueDateFrom() != 0) {
			filterQueryMap.put(" RR.DUE_DATE =", dto.getDueDateFrom() + "");
			// correct
		}
		if (dto.getInvoiceDateFrom() != 0) {
			filterQueryMap.put(" RR.INVOICE_DATE ", dto.getInvoiceDateTo() + " AND " + dto.getInvoiceDateTo());
			// correct
		}
		if (dto.getInvoiceDateTo() != 0 && dto.getInvoiceDateFrom() != 0) {
			filterQueryMap.put(" RR.INVOICE_DATE BETWEEN =", dto.getInvoiceDateFrom() + "");
			// correct
		}
		if (dto.getRequestCreatedOnTo() != 0 && dto.getRequestCreatedOnFrom() != 0) {
			filterQueryMap.put(" RR.REQUEST_UPDATED_AT =", dto.getRequestCreatedOnFrom() + "");
			// correct
		}

		if (dto.getRequestCreatedOnFrom() != 0) {
			filterQueryMap.put(" RR.REQUEST_ID IN", "('"  + dto.getRequestId() + "')");

			filterQueryMap.put(" RR.REQUEST_CREATED_AT =", "('" +dto.getRequestCreatedOnTo() +"')");
			// correct
		}

		if (dto.getInvoiceRefNum() != null && !dto.getInvoiceRefNum().isEmpty()) {
			filterQueryMap.put(" RR.EXT_INV_NUM =", dto.getInvoiceRefNum() + "");
			// correct
		}

		int lastAppendingAndIndex = filterQueryMap.size() - 1;
		AtomicInteger count = new AtomicInteger(0);
		System.err.println("lastAppendingAndIndex " + lastAppendingAndIndex);

		filterQueryMap.forEach((k, v) -> {
			System.out.println("label : " + k + " value : " + v);
			query.append(k);
			query.append(v);
			if (filterQueryMap.size() > 1) {
				if (count.getAndIncrement() < filterQueryMap.size() - 1) {
					query.append(" AND ");
				}
			} else {
				query.append(" ORDER BY RR.REQUEST_CREATED_AT DESC");
				query.append(";");
			}
		});
		if (filterQueryMap.size() > 1) {
			query.append(" ORDER BY RR.REQUEST_CREATED_AT DESC");
			query.append(";");
		}
		System.err.println("Query : Check " + query.toString());
		invoiceOrderList = invoiceHeaderRepoFilter.getFilterDetails(query.toString());
		if (invoiceOrderList != null && !invoiceOrderList.isEmpty()) {
			return invoiceOrderList;
		} else {
			return null;
		}
	}
}
