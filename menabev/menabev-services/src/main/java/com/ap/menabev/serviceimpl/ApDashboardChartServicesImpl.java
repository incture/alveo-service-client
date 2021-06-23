package com.ap.menabev.serviceimpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.ApChartUIDto;
import com.ap.menabev.dto.ApChartValues;
import com.ap.menabev.dto.ApCharts;
import com.ap.menabev.dto.ApDashboardCharts;
import com.ap.menabev.entity.InvoiceHeaderDo;
import com.ap.menabev.invoice.ApDashboardChartRepository;
import com.ap.menabev.service.ApDashboardChartServices;
import com.ap.menabev.util.ServiceUtil;

@Service
public class ApDashboardChartServicesImpl implements ApDashboardChartServices {

	private static final Logger logger = LoggerFactory.getLogger(ApDashboardChartServicesImpl.class);
	@Autowired
	ApDashboardChartRepository repo;

	@Override
	public ResponseEntity<?> getDashboardChartDetailsBetween(ApChartUIDto dto) {
		// TODO Auto-generated method stub
		try {
			List<InvoiceHeaderDo> lists = new ArrayList<InvoiceHeaderDo>();
			ApDashboardCharts apDashboardCharts = new ApDashboardCharts();
			List<ApCharts> apChartsList = new ArrayList<ApCharts>();
			int total = 0;
			ApChartValues apChartValues0 = new ApChartValues();
			ApChartValues apChartValues1 = new ApChartValues();
			ApChartValues apChartValues2 = new ApChartValues();

			List<ApChartValues> apChartValues0List = new ArrayList<ApChartValues>();
			List<ApChartValues> apChartValues1List = new ArrayList<ApChartValues>();
			List<ApChartValues> apChartValues2List = new ArrayList<ApChartValues>();
			// String myDate = "2020-04-05 05:36:40";
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String rcvdOnFrom = dto.getRcvdOnFrom();
			String rcvdOnTo = dto.getRcvdOnTo();
			Date date = new Date();
			Date date1 = new Date();
			try {
				date = dateFormat.parse(rcvdOnFrom);
				date1 = dateFormat.parse(rcvdOnTo);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			long fromDate = date.getTime();
			long toDate = date1.getTime();

			String companyCode = dto.getCompanyCode();
			String currency = dto.getCurrency();
			List<String> vendorId = dto.getVendorId();
			logger.error("from date = " + fromDate);
			logger.error("to date = " + toDate);
			logger.error("from date = " + companyCode);
			logger.error("to date = " + vendorId);

			lists = repo.getDashboardChartDetailsBetween(fromDate, toDate, companyCode, currency, vendorId);
			logger.error("list data = " + lists);
			total = lists.size();
			logger.error("total size = " + total);
			String arr[] = { "NEW", "DRAFT", "OPEN", "DUPLICATE INVOICE", "PO MISSING OR INVALID", "NO-GRN",
					"PARTIAL GRN", "UOM MISMATCH", "ITEM MISMATCH", "QTY MISMATCH", "PRICE MISMATCH", "PRICE/QTY",
					"BALANCE MISMATCH", "SAP POSTED", "PAID", "UNPAID" };
			String agingArr[] = { "1", "+1", "+7", "+14", "+21", "+28" };
			
			HashMap<String, Integer> exceptionMap = new HashMap<String, Integer>();

			HashMap<String, Integer> aigingMap = new HashMap<String, Integer>();

			Long dayCount = null;
			for (InvoiceHeaderDo list : lists) {
				//// if(list.getInvoiceStatus().equals("Vendor Report."))
				// if(!ServiceUtil.isEmpty(list.getInvoiceStatus()))
				// {
				// apChartValues0.setLabel("");
				// apChartValues0.setStatusCode(list.getVendorId());
				// apChartValues0.setStatusText(list.getVendorName());
				// apChartValues0.setCount("");
				// apChartValues0List.add(apChartValues0);
				//
				// }
				// else
				if (!ServiceUtil.isEmpty(list.getInvoiceStatus())) {
					if (exceptionMap.containsKey(list.getInvoiceStatus())) {
						exceptionMap.put(list.getInvoiceStatus(), exceptionMap.get(list.getInvoiceStatus()) + 1);
						logger.error("if Horrible Exception = " + exceptionMap);
					} else {
						exceptionMap.put(list.getInvoiceStatus(), 1);
						logger.error(" else Horrible Exception = " + exceptionMap);
					}

				}
				
				if (!ServiceUtil.isEmpty(list.getDueDate())) {

					long currentTimestamp = System.currentTimeMillis();
					try {
						dayCount = daysBetweenStringDates(currentTimestamp, list.getDueDate());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String CountToday = "1";
					String today = "+1";
					String date7 = "+7";
					String date14 = "+14";
					String date21 = "+21";
					String date28 = "+28";
					if (dayCount == 0) {
						if (aigingMap.containsKey(CountToday)) {
							aigingMap.put(CountToday, aigingMap.get(CountToday) + 1);
						} else {
							aigingMap.put(CountToday, 1);
						}
					} else {
						aigingMap.put(CountToday, 0);
						logger.error("aigingMap = " + aigingMap);
					}
					if (dayCount <= 0 && dayCount <= 7) {

						if (aigingMap.containsKey(today)) {
							aigingMap.put(today, aigingMap.get(today) + 1);
						} else {
							aigingMap.put(today, 1);
						}
					} else {aigingMap.put(today, 0);
					}
					if (dayCount >= 8 && dayCount <= 14) {
						if (aigingMap.containsKey(date7)) {
							aigingMap.put(date7, aigingMap.get(date7) + 1);
						} else {
							aigingMap.put(date7, 1);
						}
					} else {aigingMap.put(date7, 0);
					}
					if (dayCount >= 15 && dayCount <= 21) {
						if (aigingMap.containsKey(date14)) {
							aigingMap.put(date14, aigingMap.get(date14) + 1);
						} else {
							aigingMap.put(date14, 1);
						}
					} else {aigingMap.put(date14, 0);
					}
					if (dayCount >= 22 && dayCount <= 28) {
						if (aigingMap.containsKey(date21)) {
							aigingMap.put(date21, aigingMap.get(date21) + 1);
						} else {
							aigingMap.put(date21, 1);
						}
					} else {aigingMap.put(date21, 0);
					}
					if (dayCount > 28) {
						if (aigingMap.containsKey(date28)) {
							aigingMap.put(date28, aigingMap.get(date28) + 1);
						} else {
							aigingMap.put(date28, 1);
						}
					}else{
						aigingMap.put(date28, 0);
					}

				}
			}

			// for Exception payloas
				for(String s:arr)
				{
					if (exceptionMap.containsKey(s)) {
						
					} else {
						exceptionMap.put(s, 0);
					}
					
				}
				for(String s:agingArr)
				{
					if (aigingMap.containsKey(s)) {
						
					} else {
						aigingMap.put(s, 0);
					}
					
				}

			logger.error("exceptionMap = " + exceptionMap);
			logger.error("aigingMap = " + aigingMap);

			for (Map.Entry<String, Integer> entry : exceptionMap.entrySet()) {
				ApChartValues value = new ApChartValues();
				value.setLabel(entry.getKey());
				value.setStatusCode(Integer.toString(indexOf(arr, entry.getKey())));
				value.setStatusText(entry.getKey());
				value.setCount(entry.getValue());
				apChartValues1List.add(value);
			}
			logger.error("apChartValues1List = " + apChartValues1List);
			// for Aging payload

			for (Map.Entry<String, Integer> entry : aigingMap.entrySet()) {
				ApChartValues values = new ApChartValues();
				String key = entry.getKey();
				int value = entry.getValue();
				logger.error("key = " + key);
				logger.error("value = " + value);
				values.setLabel(entry.getKey());
				values.setStatusCode(entry.getKey());
				values.setStatusText("Days");
				values.setCount(entry.getValue());
				// apChartValues2.setCount(String.valueOf(entry.getValue()));
				apChartValues2List.add(values);
			}
			logger.error("apChartValues2Liste = " + apChartValues2List);
			//

			// ApCharts apCharts0 = null;
			// apCharts0.setChartName("Vendor Repor");
			// apCharts0.setLabel("Vendor
			// Repor"); apCharts0.setValues(apChartValues0List);

			ApCharts apCharts1 = new ApCharts();
			apCharts1.setChartName("Exception Repor");
			apCharts1.setLabel("Exception Repor");
			apCharts1.setValues(apChartValues1List);

			ApCharts apCharts2 = new ApCharts();
			apCharts2.setChartName("Aging report");
			apCharts2.setLabel("Aging report");
			apCharts2.setValues(apChartValues2List);

			// apChartsList.add(apCharts0);
			apChartsList.add(apCharts1);
			apChartsList.add(apCharts2);

			apDashboardCharts.setRcvdOnFrom(rcvdOnFrom);
			apDashboardCharts.setRcvdOnTo(rcvdOnTo);
			apDashboardCharts.setTotal(total);
			apDashboardCharts.setCharts(apChartsList);
			logger.error("final json = " + apDashboardCharts);
			return new ResponseEntity<ApDashboardCharts>(apDashboardCharts, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Failed due to " + e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private static int indexOf(Object[] strArray, Object element) {

		int index = Arrays.asList(strArray).indexOf(element);

		return index;

	}

	public static Long daysBetweenStringDates(long inputString1, long inputString2) throws ParseException {
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");

		if (!ServiceUtil.isEmpty(inputString2)) {

			Long diff = inputString1 - inputString2;
			return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		} else {
			return new Long("-1");
		}
	}

}