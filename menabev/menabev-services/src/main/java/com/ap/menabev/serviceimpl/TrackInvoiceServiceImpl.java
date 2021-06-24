package com.ap.menabev.serviceimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

	private static final Logger logger = LoggerFactory.getLogger(PurchaseDocumentItemServiceImpl.class);

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
		List<InvoiceHeaderDo> sapPostedList=new ArrayList<>();
		List<InvoiceHeaderDo> pendingApprovalList=new ArrayList<>();
		List<String> invoiceReferenceNumberList=new ArrayList<>();

		if(!ServiceUtil.isEmpty(headerList))
		{
		for (InvoiceHeaderDo invoiceHeaderDo:headerList){
			if(invoiceHeaderDo.getInvoiceStatus()=="13" && invoiceHeaderDo.getInvoiceStatusText()=="APPROVED")
			{
				sapPostedList.add(invoiceHeaderDo);
			}
			else
			{
				invoiceHeaderDo.setInvoiceStatus("16");
				invoiceHeaderDo.setInvoiceStatusText("Pending Approval");
				pendingApprovalList.add(invoiceHeaderDo);
			}
		 }
		for(InvoiceHeaderDo headerDo:sapPostedList ){
				invoiceReferenceNumberList.add(headerDo.getExtInvNum());
		logger.info("invoiceReferenceNumberList:"+invoiceReferenceNumberList);
		}
				//ResponseEntity<?> odataResponse=odataHelperClass.consumingOdataServiceForTrackInvoice("/sap/opu/odata/sap/ZP2P_API_INVOICESTATUS_SRV/InvoiceStatusSet", invoiceReferenceNumberList.toString(), "GET", odataHelperClass.getDestination("SD4_DEST"));
		}
		try{
		Map<String, Object> map = odataHelperClass.getDestination("SD4_DEST");
		String endPointurl = formInputUrl(invoiceReferenceNumberList);
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
	        			   
	        		   }
	        	   }
	           }
	             
	             return new ResponseEntity<TrackInvoiceOdataOutputResponse>(trackInvoiceOdataOutputResponse,HttpStatus.OK);
          }else {
           	  String jsonOutputString = (String) odataResponse.getBody();
           	TrackInvoiceOdataOutputResponse  errorMessage =  formOutPutFailureResponse(jsonOutputString);	  
           	  System.err.println("convertedRrrorResponse "+odataResponse);
  		return new ResponseEntity<TrackInvoiceOdataOutputResponse>(errorMessage,HttpStatus.BAD_REQUEST);
       }
		}
		catch(Exception e){
			logger.info(e.toString());
			return null;
		}
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
			public TrackInvoiceOdataOutputResponse formOutPutFailureResponse(String jsonOutputString)
			{
				TrackInvoiceOdataOutputResponse errorMessage = new TrackInvoiceOdataOutputResponse();
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
