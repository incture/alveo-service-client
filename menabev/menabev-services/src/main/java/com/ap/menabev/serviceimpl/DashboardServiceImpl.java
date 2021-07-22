package  com.ap.menabev.serviceimpl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.DashboardChartOutputDto;
import com.ap.menabev.dto.DashboardFilterOutputDto;
import com.ap.menabev.dto.DashboardInputDto;
import com.ap.menabev.dto.DashboardKpiOutputDto;
import com.ap.menabev.dto.DashboardOutputChartDtos;
import com.ap.menabev.dto.DashboardOutputDtoValues;
import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.entity.InvoiceHeaderDo;
import com.ap.menabev.invoice.InvoiceHeaderRepoFilter;
import com.ap.menabev.invoice.InvoiceHeaderRepository;
import com.ap.menabev.service.DashboardService;
import com.ap.menabev.util.ServiceUtil;


@Service
public class DashboardServiceImpl implements DashboardService {
	@Autowired
	private EntityManager entityManager;

	@Autowired
	private InvoiceHeaderRepository invoiceHeaderRepository;

	/*@Autowired
	private InvoiceService invoiceService;*/
	@Autowired
	private InvoiceHeaderRepoFilter invoiceHeaderRepoFilter;

	@SuppressWarnings("unchecked")
	public ResponseEntity<?> getDashboardKpiData(DashboardInputDto dto) {
		DashboardKpiOutputDto dashboardOutputDto = new DashboardKpiOutputDto();
		List<InvoiceHeaderDo> todayRecievedInvoiceDetailsCount = new ArrayList<>();
		List<InvoiceHeaderDo> invoicePendingApprovalDetailsCount = new ArrayList<>();
		List<InvoiceHeaderDo> poBasedInvoiceDetailsCount = new ArrayList<>();
		List<InvoiceHeaderDo> nonPoBasedInvoiceDetailsCount = new ArrayList<>();
		List<InvoiceHeaderDo> overDueInvoiceDetailsCount = new ArrayList<>();
		//**********************************************************************
		StringBuffer todayRecievedInvoiceDetailsQuery = new StringBuffer();
		StringBuffer invoicePendingApprovalDetailsQuery = new StringBuffer();
		StringBuffer poBasedInvoiceDetailsQuery = new StringBuffer();
		StringBuffer nonPoBasedInvoiceDetailsQuery = new StringBuffer();
		StringBuffer overDueInvoiceDetailsQuery = new StringBuffer();

		//****************************************************************************
		Map<String, String> todayRecievedInvoiceDetailsFilterQueryMap = new HashMap<String, String>();
		Map<String, String> invoicePendingApprovalDetailsFilterQueryMap = new HashMap<String, String>();
		Map<String, String> poBasedInvoiceDetailsFilterQueryMap = new HashMap<String, String>();
		Map<String, String> nonPoBasedInvoiceDetailFilterQueryMap = new HashMap<String, String>();
		Map<String, String> overDueInvoiceDetailsFilterQueryMap = new HashMap<String, String>();

		//***************************************************************************
		
		todayRecievedInvoiceDetailsQuery.append("SELECT * FROM INVOICE_HEADER RR WHERE ");
		invoicePendingApprovalDetailsQuery.append("SELECT * FROM INVOICE_HEADER RR WHERE ");
		poBasedInvoiceDetailsQuery.append("SELECT * FROM INVOICE_HEADER RR WHERE ");
		nonPoBasedInvoiceDetailsQuery.append("SELECT * FROM INVOICE_HEADER RR WHERE ");
		overDueInvoiceDetailsQuery.append("SELECT * FROM INVOICE_HEADER RR WHERE ");
		if (dto.getCompanyCode() != null && !dto.getCompanyCode().isEmpty()) {

			todayRecievedInvoiceDetailsFilterQueryMap.put(" RR.COMPANY_CODE IN", "('" + dto.getCompanyCode() + "')");
			invoicePendingApprovalDetailsFilterQueryMap.put(" RR.COMPANY_CODE IN", "('" + dto.getCompanyCode() + "')");
			poBasedInvoiceDetailsFilterQueryMap.put(" RR.COMPANY_CODE IN", "('" + dto.getCompanyCode() + "')");
			nonPoBasedInvoiceDetailFilterQueryMap.put(" RR.COMPANY_CODE IN", "('" + dto.getCompanyCode() + "')");
			overDueInvoiceDetailsFilterQueryMap.put(" RR.COMPANY_CODE IN", "('" + dto.getCompanyCode() + "')");
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
			todayRecievedInvoiceDetailsFilterQueryMap.put(" RR.VENDOR_ID IN", "(" + rqstId + ")");
			invoicePendingApprovalDetailsFilterQueryMap.put(" RR.VENDOR_ID IN", "(" + rqstId + ")");
			poBasedInvoiceDetailsFilterQueryMap.put(" RR.VENDOR_ID IN", "(" + rqstId + ")");
			nonPoBasedInvoiceDetailFilterQueryMap.put(" RR.VENDOR_ID IN", "(" + rqstId + ")");
			overDueInvoiceDetailsFilterQueryMap.put(" RR.VENDOR_ID IN", "(" + rqstId + ")");


		}
		if(!ServiceUtil.isEmpty(dto.getRcvdOnFrom()) && !ServiceUtil.isEmpty(dto.getRcvdOnTo())){
		if (dto.getRcvdOnFrom() != 0 && dto.getRcvdOnFrom() != null && dto.getRcvdOnTo() != 0
				&& dto.getRcvdOnTo() != null) {
			//todayRecievedInvoiceDetailsFilterQueryMap.put(" RR.REQUEST_CREATED_AT BETWEEN ",
			//		+dto.getRequestCreatedAt() + " AND " + dto.getRequestUpdatedAt() + "");
			invoicePendingApprovalDetailsFilterQueryMap.put(" RR.REQUEST_CREATED_AT BETWEEN ",
					+dto.getRcvdOnFrom() + " AND " + dto.getRcvdOnTo() + "");
			poBasedInvoiceDetailsFilterQueryMap.put(" RR.REQUEST_CREATED_AT BETWEEN ",
					+dto.getRcvdOnFrom() + " AND " + dto.getRcvdOnTo() + "");
			nonPoBasedInvoiceDetailFilterQueryMap.put(" RR.REQUEST_CREATED_AT BETWEEN ",
					+dto.getRcvdOnFrom() + " AND " + dto.getRcvdOnTo() + "");
			overDueInvoiceDetailsFilterQueryMap.put(" RR.REQUEST_CREATED_AT BETWEEN ",
					+dto.getRcvdOnFrom() + " AND " + dto.getRcvdOnTo() + "");
			// correct
		}
		}
		int todayRecievedInvoiceDetailsFilterQueryMaplastAppendingAndIndex = todayRecievedInvoiceDetailsFilterQueryMap.size() - 1;
		AtomicInteger todayRecievedInvoiceDetailsFilterQueryMapCount = new AtomicInteger(0);
		System.err.println("todayRecievedInvoiceDetailsFilterQueryMaplastAppendingAndIndex " + todayRecievedInvoiceDetailsFilterQueryMaplastAppendingAndIndex);
	
		Date date =new Date();

		DateFormat istFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		istFormat.setTimeZone(TimeZone.getTimeZone("IST"));

		System.out.println(istFormat.format(date));
		Calendar calendar = Calendar.getInstance();
		     calendar.setTime(date);
		     calendar.set(Calendar.HOUR_OF_DAY, 0);
		     calendar.set(Calendar.MINUTE, 0);
		     calendar.set(Calendar.SECOND, 0);
		     calendar.set(Calendar.MILLISECOND, 0);
			  Date startDate=   calendar.getTime();
			  System.out.println("dashboard fetchKPIData startDate" + startDate);
			  Date parsedValueStartDate=null;
				istFormat.setTimeZone(TimeZone.getTimeZone("IST"));

				try {
					parsedValueStartDate=istFormat.parse(startDate.toString());
					  System.out.println("dashboard fetchKPIData parsedValueStartDate" + parsedValueStartDate);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//Instant instant = Instant.ofEpochMilli(startDate.getTime());
			//	System.out.println("dashboard fetchKPIData  instant" + instant);

				// Date startDateInIST = Date.from(instant);
				//	System.out.println("dashboard fetchKPIData  startDateInIST" + startDateInIST);

				Long todayInMillSeconds=startDate.getTime();
				
				System.out.println("dashboard fetchKPIData  todayInMill" + todayInMillSeconds);
		    calendar.setTime(date);
		    calendar.set(Calendar.HOUR_OF_DAY, 23);
		    calendar.set(Calendar.MINUTE, 59);
		    calendar.set(Calendar.SECOND, 59);
		    calendar.set(Calendar.MILLISECOND, 999);
		     System.out.println("dashboard fetchKPIData  tomorow "+ calendar.getTime());
		  Date endDate=   calendar.getTime();
			System.out.println("dashboard fetchKPIData  endDate" + endDate);
			Date parsedValueendDate=null;
		  try {
			  parsedValueendDate=istFormat.parse(endDate.toString());
			     System.out.println("dashboard fetchKPIData  parsedValueendDate "+ parsedValueendDate);

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			istFormat.setTimeZone(TimeZone.getTimeZone("IST"));
			//Instant instantForEnd = Instant.ofEpochMilli(endDate.getTime());
			//System.out.println("dashboard fetchKPIData  instantForEnd" + instantForEnd);

			// Date endDateInIST = Date.from(instantForEnd);
			//	System.out.println("dashboard fetchKPIData  endDateInIST" + endDateInIST);

		  Long tomorrwInMilliSeconds=endDate.getTime();
			System.out.println("dashboard fetchKPIData tomorrwInMill" + tomorrwInMilliSeconds);

		if(todayRecievedInvoiceDetailsFilterQueryMaplastAppendingAndIndex != -1){
		todayRecievedInvoiceDetailsFilterQueryMap.forEach((k, v) -> {
			System.out.println("label : " + k + " value : " + v);
			todayRecievedInvoiceDetailsQuery.append(k);
			todayRecievedInvoiceDetailsQuery.append(v);
			if (todayRecievedInvoiceDetailsFilterQueryMap.size() > 1) {
				System.err.println("todayRecievedInvoiceDetailsFilterQueryMap  if");

				if (todayRecievedInvoiceDetailsFilterQueryMapCount.getAndIncrement() < todayRecievedInvoiceDetailsFilterQueryMap.size() - 1) {
					todayRecievedInvoiceDetailsQuery.append(" AND ");
				}
			} else if(todayRecievedInvoiceDetailsFilterQueryMap.size()==0){
				System.err.println("todayRecievedInvoiceDetailsFilterQueryMap else if");
				todayRecievedInvoiceDetailsQuery.append(" AND RR.REQUEST_CREATED_AT BETWEEN "+todayInMillSeconds+" AND "+tomorrwInMilliSeconds+" AND RR.INVOICE_STATUS IN ('0','1','2','3','4','5','6','7','8','9','10','11','12') ORDER BY RR.REQUEST_CREATED_AT DESC");
				todayRecievedInvoiceDetailsQuery.append(";");
			}
			else{
				System.err.println("todayRecievedInvoiceDetailsFilterQueryMap else");
				todayRecievedInvoiceDetailsQuery.append(" AND  RR.REQUEST_CREATED_AT BETWEEN "+todayInMillSeconds+" AND "+tomorrwInMilliSeconds+" AND RR.INVOICE_STATUS IN ('0','1','2','3','4','5','6','7','8','9','10','11','12') ORDER BY RR.REQUEST_CREATED_AT DESC");
				todayRecievedInvoiceDetailsQuery.append(";");

			}
		});
				if (todayRecievedInvoiceDetailsFilterQueryMap.size() > 1) {
			todayRecievedInvoiceDetailsQuery.append(" AND RR.REQUEST_CREATED_AT BETWEEN "+todayInMillSeconds+" AND "+tomorrwInMilliSeconds+" AND RR.INVOICE_STATUS IN ('0','1','2','3','4','5','6','7','8','9','10','11','12') ORDER BY RR.REQUEST_CREATED_AT DESC");
			todayRecievedInvoiceDetailsQuery.append(";");
		}
		System.err.println("todayRecievedInvoiceDetailsQuery Query : Check " + todayRecievedInvoiceDetailsQuery.toString());
		todayRecievedInvoiceDetailsCount= entityManager.createNativeQuery(todayRecievedInvoiceDetailsQuery.toString(), InvoiceHeaderDo.class).getResultList();
		}else{
			todayRecievedInvoiceDetailsQuery.append("  RR.REQUEST_CREATED_AT BETWEEN "+todayInMillSeconds+" AND "+tomorrwInMilliSeconds+" AND RR.INVOICE_STATUS IN ('0','1','2','3','4','5','6','7','8','9','10','11','12') ORDER BY RR.REQUEST_CREATED_AT DESC");
			todayRecievedInvoiceDetailsQuery.append(";");
			System.err.println("todayRecievedInvoiceDetailsQuery Query : Check " + todayRecievedInvoiceDetailsQuery.toString());
			todayRecievedInvoiceDetailsCount= entityManager.createNativeQuery(todayRecievedInvoiceDetailsQuery.toString(), InvoiceHeaderDo.class).getResultList();


		}
	
		//**************************************************************************************
		int invoicePendingApprovalDetailsFilterQueryMaplastAppendingAndIndex = invoicePendingApprovalDetailsFilterQueryMap.size() - 1;
		AtomicInteger invoicePendingApprovalDetailsFilterQueryMapcount = new AtomicInteger(0);
		System.err.println("lastAppendingAndIndex " + invoicePendingApprovalDetailsFilterQueryMaplastAppendingAndIndex);

		invoicePendingApprovalDetailsFilterQueryMap.forEach((k, v) -> {
			System.out.println("label : " + k + " value : " + v);
			invoicePendingApprovalDetailsQuery.append(k);
			invoicePendingApprovalDetailsQuery.append(v);
			if (invoicePendingApprovalDetailsFilterQueryMap.size() > 1) {
				if (invoicePendingApprovalDetailsFilterQueryMapcount.getAndIncrement() < invoicePendingApprovalDetailsFilterQueryMap.size() - 1) {
					invoicePendingApprovalDetailsQuery.append(" AND ");
				}
			} else if(invoicePendingApprovalDetailsFilterQueryMap.size()==0){
			invoicePendingApprovalDetailsQuery.append(" AND RR.INVOICE_STATUS IN ('0','1','2','3','4','5','6','7','8','9','10','11','12') ORDER BY RR.REQUEST_CREATED_AT DESC");
				invoicePendingApprovalDetailsQuery.append(";");
			}
		});
		if (invoicePendingApprovalDetailsFilterQueryMap.size() > 1) {
			invoicePendingApprovalDetailsQuery.append(" AND RR.INVOICE_STATUS IN ('0','1','2','3','4','5','6','7','8','9','10','11','12') ORDER BY RR.REQUEST_CREATED_AT DESC");
			invoicePendingApprovalDetailsQuery.append(";");
		}
		System.err.println("invoicePendingApprovalDetailsQuery Query : Check " + invoicePendingApprovalDetailsQuery.toString());
		invoicePendingApprovalDetailsCount= entityManager.createNativeQuery(invoicePendingApprovalDetailsQuery.toString(), InvoiceHeaderDo.class).getResultList();

		//*************************************************************************************************************
		int poBasedInvoiceDetailsFilterQueryMaplastAppendingAndIndex = poBasedInvoiceDetailsFilterQueryMap.size() - 1;
		AtomicInteger poBasedInvoiceDetailsFilterQueryMapcount = new AtomicInteger(0);
		System.err.println("poBasedInvoiceDetailsFilterQueryMaplastAppendingAndIndex " + poBasedInvoiceDetailsFilterQueryMaplastAppendingAndIndex);

		poBasedInvoiceDetailsFilterQueryMap.forEach((k, v) -> {
			System.out.println("label : " + k + " value : " + v);
			poBasedInvoiceDetailsQuery.append(k);
			poBasedInvoiceDetailsQuery.append(v);
			if (poBasedInvoiceDetailsFilterQueryMap.size() > 1) {
				if (poBasedInvoiceDetailsFilterQueryMapcount.getAndIncrement() < poBasedInvoiceDetailsFilterQueryMap.size() - 1) {
					poBasedInvoiceDetailsQuery.append(" AND ");
				}
			} else if(poBasedInvoiceDetailsFilterQueryMap.size()==0){
				poBasedInvoiceDetailsQuery.append(" AND RR.INVOICE_TYPE = 'PO' ");
				poBasedInvoiceDetailsQuery.append(";");
			}
		});
		if (poBasedInvoiceDetailsFilterQueryMap.size() > 1) {
			poBasedInvoiceDetailsQuery.append(" AND RR.INVOICE_TYPE = 'PO' ");
			poBasedInvoiceDetailsQuery.append(";");
		}
		System.err.println("poBasedInvoiceDetailsQuery Query : Check " + poBasedInvoiceDetailsQuery.toString());
		poBasedInvoiceDetailsCount= entityManager.createNativeQuery(poBasedInvoiceDetailsQuery.toString(), InvoiceHeaderDo.class).getResultList();

		//*************************************************************************************************************
		int nonPoBasedInvoiceDetailFilterQueryMaplastAppendingAndIndex = nonPoBasedInvoiceDetailFilterQueryMap.size() - 1;
		AtomicInteger nonPoBasedInvoiceDetailFilterQueryMapcount = new AtomicInteger(0);
		System.err.println("nonPoBasedInvoiceDetailFilterQueryMaplastAppendingAndIndex " + nonPoBasedInvoiceDetailFilterQueryMaplastAppendingAndIndex);

		nonPoBasedInvoiceDetailFilterQueryMap.forEach((k, v) -> {
			System.out.println("label : " + k + " value : " + v);
			nonPoBasedInvoiceDetailsQuery.append(k);
			nonPoBasedInvoiceDetailsQuery.append(v);
			if (nonPoBasedInvoiceDetailFilterQueryMap.size() > 1) {
				if (nonPoBasedInvoiceDetailFilterQueryMapcount.getAndIncrement() < nonPoBasedInvoiceDetailFilterQueryMap.size() - 1) {
					nonPoBasedInvoiceDetailsQuery.append(" AND ");
				}
			} else if (nonPoBasedInvoiceDetailFilterQueryMap.size() == 0) {
				nonPoBasedInvoiceDetailsQuery.append(" AND RR.INVOICE_TYPE='NON-PO' ");
				nonPoBasedInvoiceDetailsQuery.append(";");
			}
		});
		if (nonPoBasedInvoiceDetailFilterQueryMap.size() > 1) {
			nonPoBasedInvoiceDetailsQuery.append(" AND RR.INVOICE_TYPE='NON-PO' ");
			nonPoBasedInvoiceDetailsQuery.append(";");
		}
		System.err.println("nonPoBasedInvoiceDetailsQuery Query : Check " + nonPoBasedInvoiceDetailsQuery.toString());
		nonPoBasedInvoiceDetailsCount= entityManager.createNativeQuery(nonPoBasedInvoiceDetailsQuery.toString(), InvoiceHeaderDo.class).getResultList();

		//*************************************************************************************************************
		int overDueInvoiceDetailsFilterQueryMaplastAppendingAndIndex = overDueInvoiceDetailsFilterQueryMap.size() - 1;
		AtomicInteger overDueInvoiceDetailsFilterQueryMapcount = new AtomicInteger(0);
		System.err.println("overDueInvoiceDetailsFilterQueryMaplastAppendingAndIndex " + overDueInvoiceDetailsFilterQueryMaplastAppendingAndIndex);

		overDueInvoiceDetailsFilterQueryMap.forEach((k, v) -> {
			System.out.println("label : " + k + " value : " + v);
			overDueInvoiceDetailsQuery.append(k);
			overDueInvoiceDetailsQuery.append(v);
			if (overDueInvoiceDetailsFilterQueryMap.size() > 1) {
				if (overDueInvoiceDetailsFilterQueryMapcount.getAndIncrement() < overDueInvoiceDetailsFilterQueryMap.size() - 1) {
					overDueInvoiceDetailsQuery.append(" AND ");
				}
			} else 	if (overDueInvoiceDetailsFilterQueryMap.size() == 0) {
				overDueInvoiceDetailsQuery.append(" AND RR.INVOICE_STATUS IN ('0','1','2','3','4','5','6','7','8','9','10','11','12','13')");
				overDueInvoiceDetailsQuery.append(";");
			}
		});
		if (overDueInvoiceDetailsFilterQueryMap.size() > 1) {
			overDueInvoiceDetailsQuery.append(" AND RR.INVOICE_STATUS IN ('0','1','2','3','4','5','6','7','8','9','10','11','12','13')");
			overDueInvoiceDetailsQuery.append(";");
		}
		System.err.println("overDueInvoiceDetailsQuery Query : Check " + overDueInvoiceDetailsQuery.toString());
		overDueInvoiceDetailsCount= entityManager.createNativeQuery(overDueInvoiceDetailsQuery.toString(), InvoiceHeaderDo.class).getResultList();

		
		//*************************************************************************************************************
		DashboardFilterOutputDto dashboardFilterOutputDto=new DashboardFilterOutputDto();
		
		
		
		
		DashboardOutputDtoValues dashboardOutputDtoValues0 = new DashboardOutputDtoValues();
		DashboardOutputDtoValues dashboardOutputDtoValues1 = new DashboardOutputDtoValues();
		DashboardOutputDtoValues dashboardOutputDtoValues2 = new DashboardOutputDtoValues();
		DashboardOutputDtoValues dashboardOutputDtoValues3 = new DashboardOutputDtoValues();
		DashboardOutputDtoValues dashboardOutputDtoValues4 = new DashboardOutputDtoValues();
		List<DashboardOutputDtoValues> dashboardOutputDtoValuesList = new ArrayList<>();

		if (!ServiceUtil.isEmpty(todayRecievedInvoiceDetailsCount)) {
			System.err.println("todayRecievedInvoiceDetailsCount.getDueDate():"
					+ todayRecievedInvoiceDetailsCount.get(0).getDueDate());
			dashboardOutputDtoValues0.setLabel("Today's Pending Invoices");
			dashboardOutputDtoValues0.setIcon("");

			dashboardOutputDtoValues0.setCount(todayRecievedInvoiceDetailsCount.size());
			dashboardOutputDtoValuesList.add(dashboardOutputDtoValues0);
		} else {
			dashboardOutputDtoValues0.setLabel("Today's Pending Invoices");
			dashboardOutputDtoValues0.setIcon("");
			dashboardOutputDtoValues0.setCount(0);
			dashboardOutputDtoValuesList.add(dashboardOutputDtoValues0);
		}

		if (!ServiceUtil.isEmpty(invoicePendingApprovalDetailsCount)) {
			System.err.println("invoicePendingApprovalDetailsCount.getDueDate():"
					+ invoicePendingApprovalDetailsCount.get(0).getDueDate());

			dashboardOutputDtoValues1.setLabel("Pending Approval");
			dashboardOutputDtoValues1.setIcon("");
			dashboardOutputDtoValues1.setCount(invoicePendingApprovalDetailsCount.size());
			dashboardOutputDtoValuesList.add(dashboardOutputDtoValues1);
		} else {
			dashboardOutputDtoValues1.setLabel("Pending Approval");
			dashboardOutputDtoValues1.setIcon("");
			dashboardOutputDtoValues1.setCount(0);
			dashboardOutputDtoValuesList.add(dashboardOutputDtoValues1);
		}

		if (!ServiceUtil.isEmpty(poBasedInvoiceDetailsCount)) {
			System.err.println(
					"poBasedInvoiceDetailsCount.getDueDate():" + poBasedInvoiceDetailsCount.get(0).getDueDate());
			dashboardOutputDtoValues2.setLabel("PO Based");
			dashboardOutputDtoValues2.setIcon("");
			dashboardOutputDtoValues2.setCount(poBasedInvoiceDetailsCount.size());
			dashboardOutputDtoValuesList.add(dashboardOutputDtoValues2);
		} else {
			dashboardOutputDtoValues2.setLabel("PO Based");
			dashboardOutputDtoValues2.setIcon("");
			dashboardOutputDtoValues2.setCount(0);
			dashboardOutputDtoValuesList.add(dashboardOutputDtoValues2);
		}

		if (!ServiceUtil.isEmpty(nonPoBasedInvoiceDetailsCount)) {
			System.err.println(
					"nonPoBasedInvoiceDetailsCount.getDueDate():" + nonPoBasedInvoiceDetailsCount.get(0).getDueDate());

			dashboardOutputDtoValues3.setLabel("Non-PO Based");
			dashboardOutputDtoValues3.setIcon("");
			dashboardOutputDtoValues3.setCount(nonPoBasedInvoiceDetailsCount.size());
			dashboardOutputDtoValuesList.add(dashboardOutputDtoValues3);
		} else {
			dashboardOutputDtoValues3.setLabel("Non-PO Based");
			dashboardOutputDtoValues3.setIcon("");
			dashboardOutputDtoValues3.setCount(0);
			dashboardOutputDtoValuesList.add(dashboardOutputDtoValues3);
		}

		if (!ServiceUtil.isEmpty(overDueInvoiceDetailsCount)) {
			System.out.println("dueDate in formated:" + overDueInvoiceDetailsCount);
			System.out.println("dueDate in formated.size():" + overDueInvoiceDetailsCount.size());
			dashboardOutputDtoValues4.setLabel("Pending & Unpaid Invoices");
			dashboardOutputDtoValues4.setIcon("");
			dashboardOutputDtoValues4.setCount(overDueInvoiceDetailsCount.size());
			dashboardOutputDtoValuesList.add(dashboardOutputDtoValues4);
		} else {
			dashboardOutputDtoValues4.setLabel("Pending & Unpaid Invoices");
			dashboardOutputDtoValues4.setIcon("");
			dashboardOutputDtoValues4.setCount(0);
			dashboardOutputDtoValuesList.add(dashboardOutputDtoValues4);
		}
		dashboardOutputDto.setValues(dashboardOutputDtoValuesList);

		Integer total = todayRecievedInvoiceDetailsCount.size() + invoicePendingApprovalDetailsCount.size()
				+ poBasedInvoiceDetailsCount.size() + nonPoBasedInvoiceDetailsCount.size()
				+ overDueInvoiceDetailsCount.size();
		System.err.println("total:" + total);
		// System.err.println("dashboardOutputDtoValuesList:" +
		// dashboardOutputDtoValuesList);

		dashboardOutputDto.setChartName("KPIs");
		dashboardOutputDto.setRcvdOnFrom(dto.getRcvdOnFrom());
		dashboardOutputDto.setRcvdOnTo(dto.getRcvdOnTo());
		dashboardOutputDto.setTotal(total);

		return new ResponseEntity<DashboardKpiOutputDto>(dashboardOutputDto, HttpStatus.OK);
		// invoiceOrderList =
		// invoiceHeaderRepoFilter.getFilterDetailsForDashboard(query.toString());

	}

	
	// ***************************************************************************

	public ResponseEntity<?> getDashboardKpiChartData(DashboardInputDto dashboardInputDto) {
		DashboardFilterOutputDto filterOutputList = filterInvoicesMultipleChart(dashboardInputDto);
		List<InvoiceHeaderDto> filterOutput=filterOutputList.getQueryList();
		System.err.println("filterOutput:" + filterOutput);
		DashboardChartOutputDto dashboardChartOutputDto = new DashboardChartOutputDto();
		List<DashboardOutputChartDtos> dashboardOutputChartDtosList = new ArrayList<>();
		
		if (!ServiceUtil.isEmpty(filterOutput)) {
			List<Long> dueDateList = new ArrayList<>();
			List<Long> paymentDateList = new ArrayList<>();
			List<Long> dueDateSortedList = new ArrayList<>();
			List<Long> paymentDateTop5DateSortedList = new ArrayList<>();

			

			List<InvoiceHeaderDo> todayRecievedInvoiceDetailsCount = new ArrayList<>();

			List<DashboardOutputDtoValues> exceptionReportList = new ArrayList<>();
			List<DashboardOutputDtoValues> agingReportList = new ArrayList<>();

			int exceptionReportCounter0 = 0;
			int exceptionReportCounter1 = 0;
			int exceptionReportCounter2 = 0;
			int exceptionReportCounter3 = 0;
			int exceptionReportCounter4 = 0;
			int exceptionReportCounter5 = 0;
			int exceptionReportCounter6 = 0;
			int exceptionReportCounter7 = 0;
			int exceptionReportCounter8 = 0;
			int exceptionReportCounter9 = 0;
			int exceptionReportCounter10 = 0;
			int exceptionReportCounter11 = 0;
			int exceptionReportCounter12 = 0;
			int exceptionReportCounter13 = 0;
			int exceptionReportCounter14 = 0;
			int exceptionReportCounter15 = 0;
			int overdue = 0;
			int today = 0;
			int plus7 = 0;
			int plus14 = 0;
			int plus21 = 0;
			int agingReportCounter21 = 0;
			int plus28 = 0;
			long date = 0;
			long date7 = 0;
			long date14 = 0;
			long date21 = 0;
			long date28 = 0;
			for (InvoiceHeaderDto invoiceHeaderDto : filterOutput) {
				List<InvoiceHeaderDo> recentpayment = new ArrayList<>();
				List<InvoiceHeaderDo> agingPayment = new ArrayList<>();


				/*DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				Calendar calendar = Calendar.getInstance();

				System.out.println("dueDate in formated:" + calendar.getTime());
				LocalDateTime dueDate = Instant.ofEpochMilli(invoiceHeaderDo.getDueDate())
						.atZone(ZoneId.systemDefault()).toLocalDateTime();
				System.out.println("dueDatedueDate in formated:" + calendar.getTime());

				Date todayy = new Date();
				Long todayTimeInMillis = todayy.getTime();
				System.err.println("todayTimeInMillis:" + todayTimeInMillis);
				calendar.setTimeInMillis(todayTimeInMillis);
				System.out.println("today in formated:" + formatter.format(calendar.getTime()));

				LocalDateTime dayStartingTime = Instant.ofEpochMilli(todayTimeInMillis).atZone(ZoneId.systemDefault())
						.toLocalDateTime();
				System.out.println("dayStartingTime in formated:" + dayStartingTime);
				LocalDateTime dayEndingTime = Instant.ofEpochMilli(todayTimeInMillis).atZone(ZoneId.systemDefault())
						.toLocalDateTime().plusDays(1);
				System.out.println("dayEndingTime in formated:" + dayEndingTime);

				date = calendar.getTimeInMillis();
				System.err.println("today:" + date);
				calendar.setTimeInMillis(date);
				System.out.println("date in formated:" + formatter.format(calendar.getTime()));
				LocalDateTime datee = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDateTime();
				System.out.println("datee in formated:" + formatter.format(calendar.getTime()));*/

				Date date1 =new Date();
				Calendar calendar = Calendar.getInstance();
				System.out.println("dueDate in formated:" + calendar.getTime());
				LocalDateTime dueDate =null;
				if(!ServiceUtil.isEmpty(invoiceHeaderDto.getDueDate())){
				 dueDate = Instant.ofEpochMilli(invoiceHeaderDto.getDueDate())
						.atZone(ZoneId.systemDefault()).toLocalDateTime();
				System.out.println("dueDatedueDate in formated:" + calendar.getTime());
				}
				     calendar.setTime(date1);
				     calendar.set(Calendar.HOUR_OF_DAY, 0);
				     calendar.set(Calendar.MINUTE, 0);
				     calendar.set(Calendar.SECOND, 0);
				     calendar.set(Calendar.MILLISECOND, 0);
				     
					  Date startDate=   calendar.getTime();
					  System.out.println("dashboard fetchKPIData startDate" + startDate);
					  LocalDateTime todayInMillSeconds = Instant.ofEpochMilli(startDate.getTime())
								.atZone(ZoneId.systemDefault()).toLocalDateTime();
						System.out.println("dashboard fetchKPIData  todayInMill" + todayInMillSeconds);
				    calendar.setTime(date1);
				    calendar.set(Calendar.HOUR_OF_DAY, 23);
				    calendar.set(Calendar.MINUTE, 59);
				    calendar.set(Calendar.SECOND, 59);
				    calendar.set(Calendar.MILLISECOND, 999);
				     System.out.println("dashboard fetchKPIData  tomottow "+ calendar.getTime());
				     LocalDateTime tomorrowInMillSeconds = Instant.ofEpochMilli(startDate.getTime())
								.atZone(ZoneId.systemDefault()).toLocalDateTime();
						System.out.println("dashboard fetchKPIData  tomorrowInMillSeconds" + tomorrowInMillSeconds);
						
				calendar.add(Calendar.DATE, +7);
				System.err.println("todayDate 7: formated:" + calendar.getTime());
				date7 = calendar.getTimeInMillis();
				System.err.println("date7:" + date7);
				LocalDateTime date77 = Instant.ofEpochMilli(date7).atZone(ZoneId.systemDefault()).toLocalDateTime();
				System.out.println("date77 in formated:" + calendar.getTime());

				calendar.add(Calendar.DATE, +14);
				System.err.println("todayDate 14: formated:" + calendar.getTime());
				date14 = calendar.getTimeInMillis();
				System.err.println("date14:" + date14);
				LocalDateTime date1414 = Instant.ofEpochMilli(date14).atZone(ZoneId.systemDefault()).toLocalDateTime();
				System.out.println("date1414 in formated:" + calendar.getTime());

				calendar.add(Calendar.DATE, +21);
				System.err.println("todayDate 21: formated:" + calendar.getTime());
				date21 = calendar.getTimeInMillis();
				System.err.println("date21:" + date21);
				LocalDateTime date2121 = Instant.ofEpochMilli(date21).atZone(ZoneId.systemDefault()).toLocalDateTime();
				System.out.println("date2121 in formated:" + calendar.getTime());

				calendar.add(Calendar.DATE, +28);
				System.err.println("todayDate 28: formated:" + calendar.getTime());
				date28 = calendar.getTimeInMillis();
				System.err.println("date28:" + date28);
				LocalDateTime date2828 = Instant.ofEpochMilli(date28).atZone(ZoneId.systemDefault()).toLocalDateTime();
				System.out.println("date2828 in formated:" + calendar.getTime());

				
				  System.err.println("invoiceHeaderDo.getDueDate():" + invoiceHeaderDto.getDueDate());
				if (!ServiceUtil.isEmpty(invoiceHeaderDto.getDueDate())) {
					System.err.println("invoiceHeaderDo.getDueDate():" + invoiceHeaderDto.getDueDate());
					System.err.println("if(dueDate.isBefore(datee)):" + dueDate.isBefore(todayInMillSeconds));
					
					
					if (dueDate.isBefore(todayInMillSeconds)) {
						
						invoiceHeaderRepository.overDueInvoiceDetailsAgingReport();
						overdue++;
						System.err.println("overdue:" + overdue);
					}
					
				
					  if(dueDate.isAfter(todayInMillSeconds) &&
					  dueDate.isBefore(tomorrowInMillSeconds)) { 
							invoiceHeaderRepository.todayInvoiceDetailsAgingReport(invoiceHeaderDto.getDueDate());
						  today++;
					  System.err.println("today:" + today); }
					 

					System.err.println("datee.isAfter(date77) && dueDate.isBefore(date77):" + todayInMillSeconds.isBefore(date77)
							+ "," + dueDate.isBefore(date77));
					if (todayInMillSeconds.isAfter(date77) && dueDate.isBefore(date77)) {
						invoiceHeaderRepository.sevenPlusInvoiceDetailsAgingReport(invoiceHeaderDto.getDueDate());

						plus7++;
						System.err.println("plus7:" + plus7);
					}
					System.err.println("dueDate.isAfter(date77) && dueDate.isBefore(date1414):"
							+ dueDate.isAfter(date77) + "," + dueDate.isBefore(date1414));
					if (dueDate.isAfter(date77) && dueDate.isBefore(date1414)) {
						invoiceHeaderRepository.fourteenPlusInvoiceDetailsAgingReport(invoiceHeaderDto.getDueDate());
						plus14++;
						System.err.println("plus14:" + plus14);

					}
					System.err.println("dueDate.isAfter(date1414) && dueDate.isBefore(date2121):"
							+ dueDate.isAfter(date1414) + "," + dueDate.isBefore(date2121));
					if (dueDate.isAfter(date1414) && dueDate.isBefore(date2121)) {
						invoiceHeaderRepository.twentyOnePlusInvoiceDetailsAgingReport(invoiceHeaderDto.getDueDate());

						plus21++;
						System.err.println("plus21:" + plus21);

					}
					System.err.println("dueDate.isAfter(date2121):" + dueDate.isAfter(date2121));
					if (dueDate.isAfter(date2121)) {
						invoiceHeaderRepository.twentyEightPlusInvoiceDetailsAgingReport(invoiceHeaderDto.getDueDate());
						plus28++;
						System.err.println("plus28:" + plus28);
					}
				}

				if (!ServiceUtil.isEmpty(invoiceHeaderDto.getInvoiceStatus())) {
					switch (invoiceHeaderDto.getInvoiceStatus()) {
					case "0":
						if (invoiceHeaderDto.getInvoiceStatus().equals("0")) {
							exceptionReportCounter0++;
							System.err.println("exceptionReportCounter0:" + exceptionReportCounter0);
						}
					case "1":
						if (invoiceHeaderDto.getInvoiceStatus().equals("1")) {
							exceptionReportCounter1++;
							System.err.println("exceptionReportCounter1:" + exceptionReportCounter1);

						}
					case "2":
						if (invoiceHeaderDto.getInvoiceStatus().equals("2")) {
							exceptionReportCounter2++;
							System.err.println("exceptionReportCounter2:" + exceptionReportCounter2);

						}
					case "3":
						if (invoiceHeaderDto.getInvoiceStatus().equals("3")) {
							exceptionReportCounter3++;
							System.err.println("exceptionReportCounter3:" + exceptionReportCounter3);

						}
					case "4":
						if (invoiceHeaderDto.getInvoiceStatus().equals("4")) {
							exceptionReportCounter4++;
							System.err.println("exceptionReportCounter4:" + exceptionReportCounter4);

						}
					case "5":
						if (invoiceHeaderDto.getInvoiceStatus().equals("5")) {
							exceptionReportCounter5++;
							System.err.println("exceptionReportCounter5:" + exceptionReportCounter5);
						}
					case "6":
						if (invoiceHeaderDto.getInvoiceStatus().equals("6")) {
							exceptionReportCounter6++;
							System.err.println("exceptionReportCounter6:" + exceptionReportCounter6);

						}
					case "7":
						if (invoiceHeaderDto.getInvoiceStatus().equals("7")) {
							exceptionReportCounter7++;
							System.err.println("exceptionReportCounter7:" + exceptionReportCounter7);
						}
					case "8":
						if (invoiceHeaderDto.getInvoiceStatus().equals("8")) {
							exceptionReportCounter8++;
							System.err.println("exceptionReportCounter8:" + exceptionReportCounter8);

						}
					case "9":
						if (invoiceHeaderDto.getInvoiceStatus().equals("9")) {
							exceptionReportCounter9++;
							System.err.println("exceptionReportCounter9:" + exceptionReportCounter9);

						}
					case "10":
						if (invoiceHeaderDto.getInvoiceStatus().equals("10")) {
							exceptionReportCounter10++;
							System.err.println("exceptionReportCounter10:" + exceptionReportCounter10);

						}
					case "11":
						if (invoiceHeaderDto.getInvoiceStatus().equals("11")) {
							exceptionReportCounter11++;
							System.err.println("exceptionReportCounter11:" + exceptionReportCounter11);

						}
					case "12":
						if (invoiceHeaderDto.getInvoiceStatus().equals("12")) {
							exceptionReportCounter12++;
							System.err.println("exceptionReportCounter12:" + exceptionReportCounter12);

						}
					case "13":
						if (invoiceHeaderDto.getInvoiceStatus().equals("13")) {
							exceptionReportCounter13++;
							System.err.println("exceptionReportCounter13:" + exceptionReportCounter13);

						}
					case "14":
						if (invoiceHeaderDto.getInvoiceStatus().equals("14")) {
							exceptionReportCounter14++;
							System.err.println("exceptionReportCounter14:" + exceptionReportCounter14);

						}
					case "15":
						if (invoiceHeaderDto.getInvoiceStatus().equals("15")) {
							exceptionReportCounter15++;
							System.err.println("exceptionReportCounter15:" + exceptionReportCounter15);

						}
					}

				}

			}

			if (exceptionReportCounter0 != 0) {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(exceptionReportCounter0);
				dashboardOutputDtoValues.setLabel("New");
				dashboardOutputDtoValues.setStatusText("New");
				dashboardOutputDtoValues.setStatusCode("0");
				exceptionReportList.add(dashboardOutputDtoValues);
			}
			if (exceptionReportCounter1 != 0) {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(exceptionReportCounter1);
				dashboardOutputDtoValues.setLabel("Draft");
				dashboardOutputDtoValues.setStatusText("Draft");
				dashboardOutputDtoValues.setStatusCode("1");
				exceptionReportList.add(dashboardOutputDtoValues);
			}
			if (exceptionReportCounter2 != 0) {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(exceptionReportCounter2);
				dashboardOutputDtoValues.setLabel("Open");
				dashboardOutputDtoValues.setStatusText("Open");
				dashboardOutputDtoValues.setStatusCode("2");
				exceptionReportList.add(dashboardOutputDtoValues);

			}
			if (exceptionReportCounter3 != 0) {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(exceptionReportCounter3);
				dashboardOutputDtoValues.setLabel("Duplicate Invoice");
				dashboardOutputDtoValues.setStatusText("Duplicate Invoice");
				dashboardOutputDtoValues.setStatusCode("3");
				exceptionReportList.add(dashboardOutputDtoValues);

			}
			if (exceptionReportCounter4 != 0) {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(exceptionReportCounter4);
				dashboardOutputDtoValues.setLabel("PO Missing or Invalid");
				dashboardOutputDtoValues.setStatusText("PO Missing or Invalid");
				dashboardOutputDtoValues.setStatusCode("4");
				exceptionReportList.add(dashboardOutputDtoValues);

			}
			if (exceptionReportCounter5 != 0) {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(exceptionReportCounter5);
				dashboardOutputDtoValues.setLabel("No-GRN");
				dashboardOutputDtoValues.setStatusText("No-GRN");
				dashboardOutputDtoValues.setStatusCode("5");
				exceptionReportList.add(dashboardOutputDtoValues);

			}
			if (exceptionReportCounter6 != 0) {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(exceptionReportCounter6);
				dashboardOutputDtoValues.setLabel("Partial GRN");
				dashboardOutputDtoValues.setStatusText("Partial GRN");
				dashboardOutputDtoValues.setStatusCode("6");
				exceptionReportList.add(dashboardOutputDtoValues);

			}
			if (exceptionReportCounter7 != 0) {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(exceptionReportCounter7);
				dashboardOutputDtoValues.setLabel("UoM Mismatch");
				dashboardOutputDtoValues.setStatusText("UoM Mismatch");
				dashboardOutputDtoValues.setStatusCode("7");
				exceptionReportList.add(dashboardOutputDtoValues);

			}
			if (exceptionReportCounter8 != 0) {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(exceptionReportCounter8);
				dashboardOutputDtoValues.setLabel("Item Mismatch");
				dashboardOutputDtoValues.setStatusText("Item Mismatch");
				dashboardOutputDtoValues.setStatusCode("8");
				exceptionReportList.add(dashboardOutputDtoValues);

			}
			if (exceptionReportCounter9 != 0) {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(exceptionReportCounter9);
				dashboardOutputDtoValues.setLabel("Qty Mismatch");
				dashboardOutputDtoValues.setStatusText("Qty Mismatch");
				dashboardOutputDtoValues.setStatusCode("9");
				exceptionReportList.add(dashboardOutputDtoValues);

			}
			if (exceptionReportCounter10 != 0) {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(exceptionReportCounter10);
				dashboardOutputDtoValues.setLabel("Price Mismatch");
				dashboardOutputDtoValues.setStatusText("Price Mismatch");
				dashboardOutputDtoValues.setStatusCode("10");
				exceptionReportList.add(dashboardOutputDtoValues);

			}
			if (exceptionReportCounter11 != 0) {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(exceptionReportCounter11);
				dashboardOutputDtoValues.setLabel("Price/Qty");
				dashboardOutputDtoValues.setStatusText("Price/Qty");
				dashboardOutputDtoValues.setStatusCode("11");
				exceptionReportList.add(dashboardOutputDtoValues);

			}
			if (exceptionReportCounter12 != 0) {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(exceptionReportCounter12);
				dashboardOutputDtoValues.setLabel("Balance Mismatch");
				dashboardOutputDtoValues.setStatusText("Balance Mismatch");
				dashboardOutputDtoValues.setStatusCode("12");
				exceptionReportList.add(dashboardOutputDtoValues);

			}
			if (exceptionReportCounter13 != 0) {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(exceptionReportCounter13);
				dashboardOutputDtoValues.setLabel("Posted (Unpaid)");
				dashboardOutputDtoValues.setStatusText("Posted (Unpaid)");
				dashboardOutputDtoValues.setStatusCode("13");
				exceptionReportList.add(dashboardOutputDtoValues);

			}
			if (exceptionReportCounter14 != 0) {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(exceptionReportCounter14);
				dashboardOutputDtoValues.setLabel("paid");
				dashboardOutputDtoValues.setStatusText("paid");
				dashboardOutputDtoValues.setStatusCode("14");
				exceptionReportList.add(dashboardOutputDtoValues);

			}
			if (exceptionReportCounter15 != 0) {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(exceptionReportCounter15);
				dashboardOutputDtoValues.setLabel("Rejected");
				dashboardOutputDtoValues.setStatusText("Rejected");
				dashboardOutputDtoValues.setStatusCode("15");
				exceptionReportList.add(dashboardOutputDtoValues);

			}
			if (overdue != 0) {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(overdue);
				dashboardOutputDtoValues.setLabel("overdue");
				dashboardOutputDtoValues.setStatusText("");
				dashboardOutputDtoValues.setStatusCode("");
				agingReportList.add(dashboardOutputDtoValues);

			} else {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(overdue);
				dashboardOutputDtoValues.setLabel("overdue");
				dashboardOutputDtoValues.setStatusText("");
				dashboardOutputDtoValues.setStatusCode("");
				agingReportList.add(dashboardOutputDtoValues);

			}

			if (!ServiceUtil.isEmpty(today)) {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(today);
				dashboardOutputDtoValues.setLabel("today");
				dashboardOutputDtoValues.setStatusText("");
				dashboardOutputDtoValues.setStatusCode("");
				agingReportList.add(dashboardOutputDtoValues);

			} else {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(today);
				dashboardOutputDtoValues.setLabel("today");
				dashboardOutputDtoValues.setStatusText("");
				dashboardOutputDtoValues.setStatusCode("");
				agingReportList.add(dashboardOutputDtoValues);

			}
			if (plus7 != 0) {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(plus7);
				dashboardOutputDtoValues.setLabel("+7");
				dashboardOutputDtoValues.setStatusText("");
				dashboardOutputDtoValues.setStatusCode("");
				agingReportList.add(dashboardOutputDtoValues);
			} else {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(plus7);
				dashboardOutputDtoValues.setLabel("+7");
				dashboardOutputDtoValues.setStatusText("");
				dashboardOutputDtoValues.setStatusCode("");
				agingReportList.add(dashboardOutputDtoValues);

			}
			if (plus14 != 0) {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(plus14);
				dashboardOutputDtoValues.setLabel("+14");
				dashboardOutputDtoValues.setStatusText("");
				dashboardOutputDtoValues.setStatusCode("");
				agingReportList.add(dashboardOutputDtoValues);
			} else {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(plus14);
				dashboardOutputDtoValues.setLabel("+14");
				dashboardOutputDtoValues.setStatusText("");
				dashboardOutputDtoValues.setStatusCode("");
				agingReportList.add(dashboardOutputDtoValues);

			}
			if (plus21 != 0) {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(plus21);
				dashboardOutputDtoValues.setLabel("+21");
				dashboardOutputDtoValues.setStatusText("");
				dashboardOutputDtoValues.setStatusCode("");
				agingReportList.add(dashboardOutputDtoValues);
			} else {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(plus21);
				dashboardOutputDtoValues.setLabel("+21");
				dashboardOutputDtoValues.setStatusText("");
				dashboardOutputDtoValues.setStatusCode("");
				agingReportList.add(dashboardOutputDtoValues);

			}

			if (plus28 != 0) {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(plus28);
				dashboardOutputDtoValues.setLabel("+28");
				dashboardOutputDtoValues.setStatusText("");
				dashboardOutputDtoValues.setStatusCode("");
				agingReportList.add(dashboardOutputDtoValues);
			} else {
				DashboardOutputDtoValues dashboardOutputDtoValues = new DashboardOutputDtoValues();
				dashboardOutputDtoValues.setCount(plus28);
				dashboardOutputDtoValues.setLabel("+28");
				dashboardOutputDtoValues.setStatusText("");
				dashboardOutputDtoValues.setStatusCode("");
				agingReportList.add(dashboardOutputDtoValues);

			}

			int exceptionReportTotalCount = exceptionReportCounter0 + exceptionReportCounter1 + exceptionReportCounter2
					+ exceptionReportCounter3 + exceptionReportCounter4 + exceptionReportCounter5
					+ exceptionReportCounter6 + exceptionReportCounter7 + exceptionReportCounter8
					+ exceptionReportCounter9 + exceptionReportCounter10 + exceptionReportCounter11
					+ exceptionReportCounter12 + exceptionReportCounter13 + exceptionReportCounter14
					+ exceptionReportCounter15;
			System.err.println("exceptionReportTotalCount:" + exceptionReportTotalCount);

			int agingReportTotalCount = overdue + today + plus7 + plus14 + plus21
					+ plus28;
			System.err.println("agingReportTotalCount:" + agingReportTotalCount);
			//*************************************************************************************************
			//List<InvoiceHeaderDto> recentPayments=filterOutputList.getRecentPayentList();
			//List<InvoiceHeaderDto> recentAging=filterOutputList.getRecentAgingList();
			//*************************************************************************************************
			DashboardOutputChartDtos dashboardOutputChartAgingReport = new DashboardOutputChartDtos();
			DashboardOutputChartDtos dashboardOutputChartExceptionReport = new DashboardOutputChartDtos();
			DashboardOutputChartDtos dashboardOutputChartRecentExceptionDetails = new DashboardOutputChartDtos();
			DashboardOutputChartDtos dashboardOutputChartRecentAgingDetails = new DashboardOutputChartDtos();

			dashboardOutputChartAgingReport.setChartName("Aging Report");
			dashboardOutputChartAgingReport.setLabel("Aging Report");
			dashboardOutputChartAgingReport.setValues(agingReportList);
			System.err.println("agingReportList:" + agingReportList);
			dashboardOutputChartDtosList.add(dashboardOutputChartAgingReport);

			dashboardOutputChartExceptionReport.setChartName("Exception Report");
			dashboardOutputChartExceptionReport.setLabel("Exception Report");
			dashboardOutputChartExceptionReport.setValues(exceptionReportList);
			System.err.println("exceptionReportList:" + exceptionReportList);
			dashboardOutputChartDtosList.add(dashboardOutputChartExceptionReport);

			/*dashboardOutputChartRecentExceptionDetails.setChartName("recent payments");
			dashboardOutputChartRecentExceptionDetails.setLabel("recent payments");
			dashboardOutputChartRecentExceptionDetails.setRecentPayments(recentPayments);
			dashboardOutputChartDtosList.add(dashboardOutputChartRecentExceptionDetails);

			dashboardOutputChartRecentAgingDetails.setChartName("recent aging");
			dashboardOutputChartRecentAgingDetails.setLabel("recent aging");
			dashboardOutputChartRecentAgingDetails.setAgingPayments(recentAging);
			dashboardOutputChartDtosList.add(dashboardOutputChartRecentAgingDetails);
*/
			dashboardChartOutputDto.setRequestCreatedAt(dashboardInputDto.getRcvdOnFrom());
			dashboardChartOutputDto.setRequestUpdatedAt(dashboardInputDto.getRcvdOnTo());
			dashboardChartOutputDto.setTotalAgingReportCount(agingReportTotalCount);
			dashboardChartOutputDto.setTotalExceptioReportCount(exceptionReportTotalCount);
			dashboardChartOutputDto.setChart(dashboardOutputChartDtosList);

			return new ResponseEntity<DashboardChartOutputDto>(dashboardChartOutputDto, HttpStatus.OK);
		} else {
			dashboardChartOutputDto.setRequestCreatedAt(dashboardInputDto.getRcvdOnFrom());
			dashboardChartOutputDto.setRequestUpdatedAt(dashboardInputDto.getRcvdOnTo());
			dashboardChartOutputDto.setTotalAgingReportCount(0);
			dashboardChartOutputDto.setTotalExceptioReportCount(0);
			dashboardChartOutputDto.setChart(Collections.EMPTY_LIST);
			return new ResponseEntity<DashboardChartOutputDto>(dashboardChartOutputDto, HttpStatus.OK);

		}
	}

	

	

	private List<InvoiceHeaderDo> filterInvoicesMultiple(DashboardInputDto dto) {
		List<InvoiceHeaderDo> invoiceOrderList = null;
		StringBuffer query = new StringBuffer();
		Map<String, String> filterQueryMap = new HashMap<String, String>();

		query.append("SELECT * FROM INVOICE_HEADER RR WHERE");

		if (dto.getCompanyCode() != null && !dto.getCompanyCode().isEmpty()) {

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

		if (dto.getRcvdOnFrom() != 0 && dto.getRcvdOnFrom() != null && dto.getRcvdOnTo() != 0
				&& dto.getRcvdOnTo() != null) {
			filterQueryMap.put(" RR.REQUEST_CREATED_AT BETWEEN ",
					+dto.getRcvdOnFrom() + " AND " + dto.getRcvdOnTo() + "");
			// correct
		}

		if (dto.getCurrency() != null && !dto.getCurrency().isEmpty()) {
			filterQueryMap.put(" RR.DOCUMENT_CURRENCY =", "('" + dto.getCurrency() + "')");
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
		invoiceOrderList= entityManager.createNativeQuery(query.toString(), InvoiceHeaderDo.class).getResultList();

		if (invoiceOrderList != null && !invoiceOrderList.isEmpty()) {
			return invoiceOrderList;
		} else {
			return null;
		}
	}

	//**********************************************************************************************************
	private DashboardFilterOutputDto filterInvoicesMultipleChart(DashboardInputDto dto) {
		ModelMapper mapper=new ModelMapper();
		List<InvoiceHeaderDo> invoiceOrderList = null;
		List<InvoiceHeaderDo> recentPaymentList = null;
		List<InvoiceHeaderDo> recentAgingList = null;
		List<InvoiceHeaderDo> top5VendorDetailsList = null;

		DashboardFilterOutputDto dashboardFilterOutputDto=new DashboardFilterOutputDto();
		StringBuffer query = new StringBuffer();
		StringBuffer recentPaymentQuery = new StringBuffer();
		StringBuffer recentAgingQuery = new StringBuffer();
		StringBuffer vendorDetailsQuery = new StringBuffer();


		Map<String, String> filterQueryMap = new HashMap<String, String>();
		Map<String, String> recentPaymentFilterQueryMap= new HashMap<String, String>();
		Map<String, String> recentAgingFilterQueryMap = new HashMap<String, String>();
		Map<String, String> vendorDetailsFilterQueryMap = new HashMap<String, String>();


		query.append("SELECT * FROM INVOICE_HEADER RR WHERE ");
		recentPaymentQuery.append("SELECT * FROM INVOICE_HEADER RR WHERE ");
		recentAgingQuery.append("SELECT * FROM INVOICE_HEADER RR WHERE ");
		vendorDetailsQuery.append("SELECT * FROM INVOICE_HEADER RR WHERE ");

		if (dto.getCompanyCode() != null && !dto.getCompanyCode().isEmpty()) {

			filterQueryMap.put(" RR.COMPANY_CODE IN", "('" + dto.getCompanyCode() + "')");
			recentPaymentFilterQueryMap.put(" RR.COMPANY_CODE IN", "('" + dto.getCompanyCode() + "')");
			recentAgingFilterQueryMap.put(" RR.COMPANY_CODE IN", "('" + dto.getCompanyCode() + "')");
			vendorDetailsFilterQueryMap.put(" RR.COMPANY_CODE IN", "('" + dto.getCompanyCode() + "')");

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
			recentPaymentFilterQueryMap.put(" RR.VENDOR_ID IN", "(" + rqstId + ")");
			recentAgingFilterQueryMap.put(" RR.VENDOR_ID IN", "(" + rqstId + ")");
			vendorDetailsFilterQueryMap.put(" RR.VENDOR_ID IN", "(" + rqstId + ")");

		}
if(!ServiceUtil.isEmpty(dto.getRcvdOnFrom()) && !ServiceUtil.isEmpty(dto.getRcvdOnTo())){
		if (dto.getRcvdOnFrom() != 0 && dto.getRcvdOnFrom() != null && dto.getRcvdOnTo() != 0
				&& dto.getRcvdOnTo() != null) {
			filterQueryMap.put(" RR.REQUEST_CREATED_AT BETWEEN ",
					+dto.getRcvdOnFrom() + " AND " + dto.getRcvdOnTo() + "");
			recentPaymentFilterQueryMap.put(" RR.REQUEST_CREATED_AT BETWEEN ",
					+dto.getRcvdOnFrom() + " AND " + dto.getRcvdOnTo() + "");
			recentAgingFilterQueryMap.put(" RR.REQUEST_CREATED_AT BETWEEN ",
					+dto.getRcvdOnFrom() + " AND " + dto.getRcvdOnTo() + "");
			vendorDetailsFilterQueryMap.put(" RR.REQUEST_CREATED_AT BETWEEN ",
					+dto.getRcvdOnFrom() + " AND " + dto.getRcvdOnTo() + "");
			// correct
		}
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
		System.err.println("Queryyyyyyyyyyyyyyyyyyyyy : Check " + query.toString());
		invoiceOrderList= entityManager.createNativeQuery(query.toString(), InvoiceHeaderDo.class).getResultList();
		List<InvoiceHeaderDto> invoiceOrderDtoList=new ArrayList<>();
		for(InvoiceHeaderDo InvoiceHeaderDo:invoiceOrderList){
			InvoiceHeaderDto invoiceHeaderDto=	mapper.map(InvoiceHeaderDo, InvoiceHeaderDto.class);
			invoiceOrderDtoList.add(invoiceHeaderDto);
		}
		
		//**************************************************************************************
/*		int lastAppendingAndIndexRecentPayment = recentPaymentFilterQueryMap.size() - 1;
		AtomicInteger countRecentPayment = new AtomicInteger(0);
		System.err.println("lastAppendingAndIndexRecentPayment " + lastAppendingAndIndexRecentPayment);

		recentPaymentFilterQueryMap.forEach((k, v) -> {
			System.out.println("label : " + k + " value : " + v);
			recentPaymentQuery.append(k);
			recentPaymentQuery.append(v);
			if (recentPaymentFilterQueryMap.size() > 1) {
				if (countRecentPayment.getAndIncrement() < recentPaymentFilterQueryMap.size() - 1) {
					recentPaymentQuery.append(" AND ");
				}
			} else if(recentPaymentFilterQueryMap.size() == 0){
				recentPaymentQuery.append(" AND RR.INVOICE_STATUS='14' ORDER BY RR.PAYMENT_DATE DESC limit 5");
				recentPaymentQuery.append(";");
			}
		});
		if (recentPaymentFilterQueryMap.size() > 1) {
			recentPaymentQuery.append(" AND RR.INVOICE_STATUS='14' ORDER BY RR.PAYMENT_DATE DESC limit 5");
			recentPaymentQuery.append(";");
		}
		System.err.println("recentPaymentFilterQueryMap : Check " + recentPaymentQuery.toString());
		recentPaymentList= entityManager.createNativeQuery(recentPaymentQuery.toString(), InvoiceHeaderDo.class).getResultList();
		List<InvoiceHeaderDto> recentPaymentDtoList=new ArrayList<>();
		for(InvoiceHeaderDo InvoiceHeaderDo:recentPaymentList){
			InvoiceHeaderDto invoiceHeaderDto=	mapper.map(InvoiceHeaderDo, InvoiceHeaderDto.class);
			recentPaymentDtoList.add(invoiceHeaderDto);
		}*/
		
		
		//**************************************************************************************************
		
		/*int lastAppendingAndIndexRecentAging = recentAgingFilterQueryMap.size() - 1;
		AtomicInteger countRecentAging = new AtomicInteger(0);
		System.err.println("lastAppendingAndIndexRecentAging " + lastAppendingAndIndexRecentAging);

		recentAgingFilterQueryMap.forEach((k, v) -> {
			System.out.println("label : " + k + " value : " + v);
			recentAgingQuery.append(k);
			recentAgingQuery.append(v);
			if (recentPaymentFilterQueryMap.size() > 1) {
				if (countRecentAging.getAndIncrement() < recentAgingFilterQueryMap.size() - 1) {
					recentAgingQuery.append(" AND ");
				}
			}else if(recentPaymentFilterQueryMap.size() == 0){
				recentAgingQuery.append(" AND RR.INVOICE_STATUS IN ('0','1','2','3','4','5','6','7','8','9','10','11','12','13') ORDER BY RR.INVOICE_DATE DESC limit 5");
				recentAgingQuery.append(";");
			}
		});
		if (recentAgingFilterQueryMap.size() > 1) {
			recentAgingQuery.append(" AND RR.INVOICE_STATUS IN ('0','1','2','3','4','5','6','7','8','9','10','11','12','13') ORDER BY RR.INVOICE_DATE DESC limit 5");
			recentAgingQuery.append(";");
		}
		System.err.println("recentAgingQuery : Check " + recentAgingQuery.toString());
		recentAgingList= entityManager.createNativeQuery(recentAgingQuery.toString(), InvoiceHeaderDo.class).getResultList();
		List<InvoiceHeaderDto> recentAgingDtoList=new ArrayList<>();
		for(InvoiceHeaderDo InvoiceHeaderDo:recentAgingList){
			InvoiceHeaderDto invoiceHeaderDto=	mapper.map(InvoiceHeaderDo, InvoiceHeaderDto.class);
			recentAgingDtoList.add(invoiceHeaderDto);
		}*/
		
		//**********************************************************************************************************
		
		int lastAppendingAndIndexvendorDetailsFilterQueryMap = vendorDetailsFilterQueryMap.size() - 1;
		AtomicInteger countvendorDetailsFilterQueryMap = new AtomicInteger(0);
		System.err.println("lastAppendingAndIndexRecentAging " + lastAppendingAndIndexvendorDetailsFilterQueryMap);

		vendorDetailsFilterQueryMap.forEach((k, v) -> {
			System.out.println("label : " + k + " value : " + v);
			vendorDetailsQuery.append(k);
			recentAgingQuery.append(v);
			if (vendorDetailsFilterQueryMap.size() > 1) {
				if (countvendorDetailsFilterQueryMap.getAndIncrement() < vendorDetailsFilterQueryMap.size() - 1) {
					vendorDetailsQuery.append(" AND ");
				}
			}else if(vendorDetailsFilterQueryMap.size() == 0){
				vendorDetailsQuery.append(" AND RR.INVOICE_STATUS IN ('0','1','2','3','4','5','6','7','8','9','10','11','12','13') ORDER BY RR.INVOICE_DATE DESC limit 5");
				vendorDetailsQuery.append(";");
			}
		});
		if (vendorDetailsFilterQueryMap.size() > 1) {
			vendorDetailsQuery.append(" AND RR.INVOICE_STATUS IN ('0','1','2','3','4','5','6','7','8','9','10','11','12','13') ORDER BY RR.INVOICE_DATE DESC limit 5");
			vendorDetailsQuery.append(";");
		}
		System.err.println("vendorDetailsQuery : Check " + vendorDetailsQuery.toString());
		top5VendorDetailsList= entityManager.createNativeQuery(vendorDetailsQuery.toString(), InvoiceHeaderDo.class).getResultList();
		List<InvoiceHeaderDto> top5VendorDetailsDtoList=new ArrayList<>();
		for(InvoiceHeaderDo InvoiceHeaderDo:recentAgingList){
			InvoiceHeaderDto invoiceHeaderDto=	mapper.map(InvoiceHeaderDo, InvoiceHeaderDto.class);
			top5VendorDetailsDtoList.add(invoiceHeaderDto);
		}
		dashboardFilterOutputDto.setQueryList(invoiceOrderDtoList);
		//dashboardFilterOutputDto.setRecentPayentList(recentPaymentDtoList);
		//dashboardFilterOutputDto.setRecentAgingList(recentAgingDtoList);
		dashboardFilterOutputDto.setVendorDeatailsCount(top5VendorDetailsDtoList);
		
			return dashboardFilterOutputDto;
	}

	
}
