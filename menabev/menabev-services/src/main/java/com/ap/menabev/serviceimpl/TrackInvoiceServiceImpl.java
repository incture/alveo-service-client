package com.ap.menabev.serviceimpl;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.OdataTrackInvoiceObject;
import com.ap.menabev.dto.OdataTrackInvoiceOutputPayload;
import com.ap.menabev.dto.OdataTrackInvoiceResponseDto;
import com.ap.menabev.dto.TrackInvoiceInputDto;
import com.ap.menabev.dto.TrackInvoiceOdataOutputResponse;
import com.ap.menabev.dto.TrackInvoiceOutputPayload;
import com.ap.menabev.entity.InvoiceHeaderDo;
import com.ap.menabev.invoice.InvoiceHeaderRepoFilter;
import com.ap.menabev.invoice.InvoiceHeaderRepository;
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
		List<InvoiceHeaderDo> headerList = filterInvoicesMultiple(trackInvoiceInputDto);
		System.err.println("headerList :" + headerList);
		ModelMapper modelMapper = new ModelMapper();
		List<InvoiceHeaderDto> sapPostedList = new ArrayList<>();
		List<InvoiceHeaderDto> pendingApprovalList = new ArrayList<>();
		List<InvoiceHeaderDto> rejectedList = new ArrayList<>();
		List<String> invoiceReferenceNumberList = new ArrayList<>();
		List<InvoiceHeaderDto> finalPayload = new ArrayList<>();

		if (!ServiceUtil.isEmpty(headerList)) {
			System.err.println("headerList invoiceNumber:" + headerList.get(0).getInvoice_ref_number());
			for (InvoiceHeaderDo invoiceHeaderDo : headerList) {
				if (invoiceHeaderDo.getInvoiceStatus().equals("13")
						&& invoiceHeaderDo.getInvoiceStatus().equals("14")) {
					System.err.println("headerList sapPostedDto:" + invoiceHeaderDo.getInvoiceStatus());
					double total = 0;
					if (!ServiceUtil.isEmpty(invoiceHeaderDo.getGrossAmount())
							&& !ServiceUtil.isEmpty(invoiceHeaderDo.getTaxAmount())) {
						total = invoiceHeaderDo.getGrossAmount() + invoiceHeaderDo.getTaxAmount();
					}
					InvoiceHeaderDto sapPostedDto = modelMapper.map(invoiceHeaderDo, InvoiceHeaderDto.class);
					sapPostedDto.setInvoiceTotal(total);
					System.err.println("headerList sapPostedDto:" + sapPostedDto);

					sapPostedList.add(sapPostedDto);
				} else if (invoiceHeaderDo.getInvoiceStatus().equals("15")) {
					System.err.println("headerList rejectedDto:" + invoiceHeaderDo.getInvoiceStatus());
					double total = 0;
					if (!ServiceUtil.isEmpty(invoiceHeaderDo.getGrossAmount())
							&& !ServiceUtil.isEmpty(invoiceHeaderDo.getTaxAmount())) {
						total = invoiceHeaderDo.getGrossAmount() + invoiceHeaderDo.getTaxAmount();
					}
					InvoiceHeaderDto sapRejectedDto = modelMapper.map(invoiceHeaderDo, InvoiceHeaderDto.class);
					System.err.println("headerList rejectedDto:" + sapRejectedDto);
					sapRejectedDto.setInvoiceTotal(total);
					rejectedList.add(sapRejectedDto);
				} else {
					System.err.println("headerList pendingApprovalDto:" + invoiceHeaderDo.getInvoiceStatus());
					double total = 0;
					if (!ServiceUtil.isEmpty(invoiceHeaderDo.getGrossAmount())
							&& !ServiceUtil.isEmpty(invoiceHeaderDo.getTaxAmount())) {
						total = invoiceHeaderDo.getGrossAmount() + invoiceHeaderDo.getTaxAmount();
					}
					InvoiceHeaderDto sapPendingApprovaldDto = modelMapper.map(invoiceHeaderDo, InvoiceHeaderDto.class);
					sapPendingApprovaldDto.setInvoiceTotal(total);
					sapPendingApprovaldDto.setInvoiceStatus("16");
					sapPendingApprovaldDto.setInvoiceStatusText("Pending Approval");
					System.err.println("headerList pendingApprovalDto:" + sapPendingApprovaldDto);

					pendingApprovalList.add(sapPendingApprovaldDto);
				}
			}
			if (!ServiceUtil.isEmpty(sapPostedList)) {
				for (InvoiceHeaderDto headerDo : sapPostedList) {
					invoiceReferenceNumberList.add(headerDo.getExtInvNum());
					System.err.println("invoiceReferenceNumberList:" + invoiceReferenceNumberList);
				}
				// ResponseEntity<?>
				// odataResponse=odataHelperClass.consumingOdataServiceForTrackInvoice("/sap/opu/odata/sap/ZP2P_API_INVOICESTATUS_SRV/InvoiceStatusSet",
				// invoiceReferenceNumberList.toString(), "GET",
				// odataHelperClass.getDestination("SD4_DEST"));
			}

			if (!ServiceUtil.isEmpty(invoiceReferenceNumberList)) {
				try {
					Map<String, Object> map = odataHelperClass.getDestination("SD4_DEST");
					String endPointurl = formInputUrl(invoiceReferenceNumberList);
					System.err.println("endPointurl:" + endPointurl);
					ResponseEntity<?> odataResponse = odataHelperClass.consumingOdataService(endPointurl, null, "GET",
							map);
					if (odataResponse.getStatusCodeValue() == 200) {
						String jsonOutputString = (String) odataResponse.getBody();
						System.err.println("ECCResponse " + jsonOutputString);
						// convert OuputResponse
						TrackInvoiceOdataOutputResponse trackInvoiceOdataOutputResponse = formOutPutSuccessResponse(
								jsonOutputString);
						System.err.println("convertedResponse " + trackInvoiceOdataOutputResponse);
						if (!ServiceUtil.isEmpty(trackInvoiceOdataOutputResponse.getUsers())) {
							for (OdataTrackInvoiceObject odataTrackInvoiceObject : trackInvoiceOdataOutputResponse
									.getUsers()) {
								if (!ServiceUtil.isEmpty(odataTrackInvoiceObject.getClearingDate())) {

									for (InvoiceHeaderDto invoiceHeaderDto : sapPostedList) {
										invoiceHeaderDto.setInvoiceStatus("14");
										invoiceHeaderDto.setInvoiceStatusText("Paid");
										invoiceHeaderDto.setClearingDate(odataTrackInvoiceObject.getClearingDate());
										invoiceHeaderDto
												.setPaymentReference(odataTrackInvoiceObject.getPaymentStatus());
										sapPostedList.add(invoiceHeaderDto);
									}
								}
							}
						}
					} else {
						String jsonOutputStr = (String) odataResponse.getBody();
						TrackInvoiceOutputPayload errorMessage = formOutPutFailureResponse(jsonOutputStr);
						System.err.println("convertedRrrorResponse " + odataResponse);
						// return new
						// ResponseEntity<TrackInvoiceOutputPayload>(errorMessage,HttpStatus.BAD_REQUEST);
					}
				} catch (Exception e) {
					e.printStackTrace();
					// return null;
				}
			} else {

				TrackInvoiceOutputPayload trackInvoiceOutputPayload = new TrackInvoiceOutputPayload();
				trackInvoiceOutputPayload.setMessage("Record not found");
				trackInvoiceOutputPayload.setType("200");
				trackInvoiceOutputPayload.setPayload(Collections.EMPTY_LIST);
				System.err.println("Refrerence number is not found for the specipic vendor number");
				// return new
				// ResponseEntity<TrackInvoiceOutputPayload>(trackInvoiceOutputPayload,HttpStatus.OK);
			}
			List<InvoiceHeaderDto> newList = Stream.of(sapPostedList, pendingApprovalList, rejectedList)
					.flatMap(Collection::stream).collect(Collectors.toList());
			System.err.println("newList:" + newList);
			TrackInvoiceOutputPayload trackInvoiceOutputPayload = new TrackInvoiceOutputPayload();
			trackInvoiceOutputPayload.setPayload(newList);
			trackInvoiceOutputPayload.setType("200");
			trackInvoiceOutputPayload.setMessage("Success");
			return new ResponseEntity<TrackInvoiceOutputPayload>(trackInvoiceOutputPayload, HttpStatus.OK);
		} else {
			TrackInvoiceOutputPayload trackInvoiceOutputPayload = new TrackInvoiceOutputPayload();
			trackInvoiceOutputPayload.setMessage("Record not found");
			trackInvoiceOutputPayload.setType("200");
			trackInvoiceOutputPayload.setPayload(Collections.EMPTY_LIST);
			System.err.println("filtered values not found in DB");
			return new ResponseEntity<TrackInvoiceOutputPayload>(trackInvoiceOutputPayload, HttpStatus.OK);

		}
	}

	public TrackInvoiceOdataOutputResponse formOutPutSuccessResponse(String jsonOutputString) {
		TrackInvoiceOdataOutputResponse trackInvoiceOdataOutputResponse = new TrackInvoiceOdataOutputResponse();
		trackInvoiceOdataOutputResponse.setType("TEST_SUCCESS");
		trackInvoiceOdataOutputResponse.setMessage("Success");
		List<OdataTrackInvoiceObject> trackInvoiceOutputDtoList = new ArrayList<OdataTrackInvoiceObject>();
		OdataTrackInvoiceResponseDto taskDto = convertStringToJsonForOdataSuccess(jsonOutputString);
		OdataTrackInvoiceOutputPayload outResp = taskDto.getD();
		List<OdataTrackInvoiceObject> resultList = outResp.getResults();
		resultList.stream().forEach(r -> {
			trackInvoiceOutputDtoList.add(r);
		});
		trackInvoiceOdataOutputResponse.setUsers(trackInvoiceOutputDtoList);
		return trackInvoiceOdataOutputResponse;
	}

	// Faiure
	public TrackInvoiceOutputPayload formOutPutFailureResponse(String jsonOutputString) {
		TrackInvoiceOutputPayload errorMessage = new TrackInvoiceOutputPayload();
		OdataErrorResponseDto response = convertStringToJsonForOdataFailure(jsonOutputString);
		errorMessage.setType("Odata service failed");
		errorMessage.setMessage(response.getError().getMessage().getValue());
		errorMessage.setPayload(Collections.emptyList());

		return errorMessage;

	}

	// get odata success body
	public OdataTrackInvoiceResponseDto convertStringToJsonForOdataSuccess(String json) {
		OdataTrackInvoiceResponseDto taskDto = new Gson().fromJson(json.toString(), OdataTrackInvoiceResponseDto.class);
		return taskDto;
	}

	// get odata Failure body
	public OdataErrorResponseDto convertStringToJsonForOdataFailure(String json) {
		OdataErrorResponseDto response = new Gson().fromJson(json.toString(), OdataErrorResponseDto.class);
		return response;
	}

	public String formInputUrl(List<String> invoiceReferenceNumberList) {
		StringBuilder urlForm = new StringBuilder();
		appendParamInOdataUrl(urlForm, "&$format", "json");
		appendParamInOdataUrl(urlForm, "&$filter", "");
		appendValuesInOdataUrl(urlForm, "ReferenceInvoiceNumber", invoiceReferenceNumberList);
		// appendInOdataUrl(urlForm, ")and","(" );
		urlForm.insert(0, ("/sap/opu/odata/sap/ZP2P_API_INVOICESTATUS_SRV/InvoiceStatusSet?"));
		System.err.println("url" + urlForm.toString());
		return urlForm.toString();
	}

	public static void appendValuesInOdataUrl(StringBuilder url, String key, List<String> value) {
		for (int i = 0; i < value.size(); i++) {
			if (value.size() == 1) {
				url.append(key + "%20eq%20" + "%27" + value.get(i) + "%27");
				System.out.println("26 ");
			} else if (i == value.size() - 1) {
				url.append(key + "%20eq%20" + "%27" + value.get(i) + "%27");
				System.out.println("30 ");
			} else {
				url.append(key + "%20eq%20" + "%27" + value.get(i) + "%27" + "%20or%20");
				System.out.println("29 ");

			}
		}
	}

	public static void appendParamInOdataUrl(StringBuilder url, String key, String value) {
		url.append(key + "=" + value);
	}

	public static void appendInOdataUrl(StringBuilder url, String key, String value) {
		url.append(key + "" + value);
	}

	private List<InvoiceHeaderDo> filterInvoicesMultiple(TrackInvoiceInputDto dto) {
		List<InvoiceHeaderDo> invoiceOrderList = null;
		StringBuffer query = new StringBuffer();
		Map<String, String> filterQueryMap = new HashMap<String, String>();

		query.append("SELECT * FROM INVOICE_HEADER RR WHERE");
		if (dto.getRequestId() != null && !dto.getRequestId().isEmpty()) {

			filterQueryMap.put(" RR.REQUEST_ID =", "('" + dto.getRequestId() + "')");
		}
		if (dto.getCompanyCode() != null && !dto.getCompanyCode().isEmpty()) {

			filterQueryMap.put(" RR.COMPANY_CODE =", "('" + dto.getCompanyCode() + "')");
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

		if (dto.getInvoiceDateFrom() != 0 && dto.getInvoiceDateTo() != 0) {
			filterQueryMap.put(" RR.INVOICE_DATE BETWEEN ",
					+dto.getInvoiceDateFrom() + " AND " + dto.getInvoiceDateTo());
			// correct
		}

		if (dto.getRequestCreatedOnTo() != 0 && dto.getRequestCreatedOnFrom() != 0) {
			filterQueryMap.put(" RR.REQUEST_CREATED_AT BETWEEN ",
					+dto.getRequestCreatedOnFrom() + " AND " + dto.getRequestCreatedOnTo());
			// correct
		}

		if (dto.getInvoiceRefNum() != null && !dto.getInvoiceRefNum().isEmpty()) {
			filterQueryMap.put(" RR.EXT_INV_NUM =", "('" + dto.getInvoiceRefNum() + "')");
			// correct
		}
		if (dto.getInvoiceStatus() != null && !dto.getInvoiceStatus().isEmpty()) {
			StringBuffer rqstId = new StringBuffer();
			for (int i = 0; i < dto.getInvoiceStatus().size(); i++) {
				if (i < dto.getInvoiceStatus().size() - 1) {
					rqstId.append("'" + dto.getInvoiceStatus().get(i) + "'" + ",");
				} else {
					rqstId.append("'" + dto.getInvoiceStatus().get(i) + "'");
				}
			}
			filterQueryMap.put(" RR.INVOICE_STATUS IN", "(" + rqstId + ")");
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

	public ResponseEntity<?> downloadExcel(TrackInvoiceInputDto dto) throws IOException {
		ModelMapper modelMapper = new ModelMapper();
		List<InvoiceHeaderDo> filterOutput = filterInvoicesMultiple(dto);

		String[] columns = { "Invoice reference number", "Invoice Date", "Vendor", "CompanyCode", "GrossAmount", "Tax",
				"Net Amount", "Payment Reference Number", "Due Date", "Invoice Status" };
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("InvoiceDetailsSheet");
		// Create a Font for styling header cells
		Font headerFont = workbook.createFont();
		// headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 14);
		// Create a CellStyle with the font
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		// Create a Row
		Row headerRow = sheet.createRow(0);
		// Creating cells
		for (int i = 0; i < columns.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(columns[i]);
			cell.setCellStyle(headerCellStyle);
		}

		int rowNum = 1;
		for (InvoiceHeaderDo invoiceHeaderDo : filterOutput) {
			InvoiceHeaderDto invoiceHeaderDto = modelMapper.map(invoiceHeaderDo, InvoiceHeaderDto.class);

			Row row = sheet.createRow(rowNum);

			row.createCell(0).setCellValue(invoiceHeaderDto.getInvoice_ref_number());
			System.err.println(invoiceHeaderDto.getInvoice_ref_number());

			LocalDateTime invoiceDate = Instant.ofEpochMilli(invoiceHeaderDto.getInvoiceDate())
					.atZone(ZoneId.systemDefault()).toLocalDateTime();
			System.out.println("invoiceDate in formatedOrder:" + invoiceDate);

			row.createCell(1).setCellValue(invoiceDate.toString());
			System.err.println("invoiceDate.toString():" + invoiceDate.toString());
			row.createCell(2).setCellValue(invoiceHeaderDto.getVendorId());
			System.err.println(invoiceHeaderDto.getVendorId());

			row.createCell(3).setCellValue(invoiceHeaderDto.getCompCode());
			row.createCell(4).setCellValue(invoiceHeaderDto.getGrossAmount().doubleValue());
			row.createCell(5).setCellValue(invoiceHeaderDto.getTaxAmount().doubleValue());
			row.createCell(6).setCellValue(invoiceHeaderDto.getInvoiceTotal().doubleValue());
			row.createCell(7).setCellValue(invoiceHeaderDto.getPaymentReference());

			LocalDateTime dueDate = Instant.ofEpochMilli(invoiceHeaderDto.getDueDate()).atZone(ZoneId.systemDefault())
					.toLocalDateTime();
			System.out.println("dueDatedueDate in formatedOrder:" + dueDate);
			row.createCell(8).setCellValue(dueDate.toString());

			row.createCell(9).setCellValue(invoiceHeaderDto.getInvoiceStatus());

			rowNum++;
		}
		for (int i = 0; i < columns.length; i++) {
			sheet.autoSizeColumn(i);
		}
		FileOutputStream fileOut1 = new FileOutputStream("InvoiceDetails.xlsx");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		workbook.write(outputStream);
		fileOut1.close();
		workbook.close();
		byte[] b1 = outputStream.toByteArray();
		String encode = Base64.getEncoder().encodeToString(b1);
		return new ResponseEntity<String>(encode, HttpStatus.OK);

		// return ResponseEntity.ok().body(new Response<String>(encode));
	}
}
