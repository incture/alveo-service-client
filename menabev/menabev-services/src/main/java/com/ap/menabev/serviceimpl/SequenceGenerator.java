package com.ap.menabev.serviceimpl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ap.menabev.entity.SequenceGeneratorEntity;
import com.ap.menabev.service.SequenceGeneratorService;
import com.ap.menabev.service.SequenceGeneratorServices;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.DateUtils;
import com.ap.menabev.util.MenabevApplicationConstant;

/**
 * @author Santosh.Singh This class is used to generate the custom sequence for
 *         multiple tables.
 */
@Service
public class SequenceGenerator implements SequenceGeneratorServices{

	public final static ZoneId ZONE_ID = ZoneId.systemDefault();

	// Counter for exchange sequence
	private AtomicInteger invoiceSeqValue = new AtomicInteger(1);

	// Counter for return sequence

	private volatile LocalDate lastDate = LocalDate.now(ZONE_ID);

	private volatile static SequenceGenerator instance = null;

	// set to true when application or server started or restarted.
	public boolean isStarted = true;

	
	
	@Autowired
	private SequenceGeneratorService service;
	public SequenceGenerator() {
		// private constructor to prevent object creation from outside of this
		// class
	}

	
	// Returning single instance of class in application
	@Override
	public  SequenceGenerator getInstance() {
		if (instance == null) {
			synchronized (SequenceGenerator.class) {
				if (instance == null) {
					instance = new SequenceGenerator();
				}
			}
		}
		return instance;
	}

	/**
	 * This method is used to build the sequence according to the prefix passed
	 * as argument.
	 * 
	 * @param seqCode
	 * @return sequence String.
	 */
	public String getNext(String seqCode, String mappingId) {
		LocalDate todaysDate = LocalDate.now(ZONE_ID);
		String seq;

		// checking for date change
		if (!lastDate.equals(todaysDate)) {
			synchronized (this) {
				if (!lastDate.equals(todaysDate)) {
					lastDate = todaysDate;
					// Reset all counters to default if date changed.
					resetAll();
				}
			}
		}
		synchronized (this) {
			// Getting the counter w.r.t PREFIX.
			int seqVal = getCounterVariable(seqCode).getAndIncrement();

			// Preparing the pattern.
			seq = seqCode+"-" + todaysDate.format(DateTimeFormatter.ofPattern(DateUtils.SEQ_DATE_FORMAT))
			+"-"+String.format("%08d", seqVal);
		}

		return seq;
	}

	/**
	 * Resetting all the values to it's default state
	 */
	private void resetAll() {
		invoiceSeqValue.set(1);
		
		
	}

	/**
	 * This method returns the locally managed counter variable w.r.t. PREFIX.
	 * 
	 * @param seqCode
	 * @return counter AtomicInteger.
	 */
	private AtomicInteger getCounterVariable(String seqCode) {
		AtomicInteger counter = null;

		switch (seqCode) {
		case ApplicationConstants.INVOICE_SEQUENCE:
			counter = invoiceSeqValue;
			break;

		default:
			break;
		}
		return counter;
	}
	

	/**
	 * This method is used to update all the counter variables to it's current
	 * count. It's immediately called after application is
	 * started/restarted/reset.
	 * 
	 * @param list
	 */
	public void reAssignValue(List<SequenceGeneratorEntity> list) {
		if (list.size() > 0) {
			for (SequenceGeneratorEntity data : list) {
				// Setting values to all variables one by one
				setCounterVariable(data.getSeqCode(), data.getCounter());
			}
		}

		isStarted = false;
	}
	
	public void reAssignValueByMappingId(List<SequenceGeneratorEntity> list) {
		if (list.size() > 0) {
			for (SequenceGeneratorEntity data : list) {
				// Setting values to all variables one by one
				setCounterVariable(data.getSeqCode(), data.getCounter());
			}
		}

		isStarted = false;
	}

	/**
	 * Used to set the count value w.r.t PREFIX.
	 * 
	 * @param seqCode
	 * @param count
	 */
	private void setCounterVariable(String seqCode, int count) {

		switch (seqCode) {
		case ApplicationConstants.INVOICE_SEQUENCE:
			invoiceSeqValue.set(++count);
			break;

		default:
			break;
		}
	}

	public String getNextByMappingId(String seqCode, String mappingId) {
		LocalDate todaysDate = LocalDate.now(ZONE_ID);
		String seq;

		// checking for date change
		if (!lastDate.equals(todaysDate)) {
			synchronized (this) {
				if (!lastDate.equals(todaysDate)) {
					lastDate = todaysDate;
					// Reset all counters to default if date changed.
					resetAll();
				}
			}
		}
		synchronized (this) {
			// Getting the counter w.r.t PREFIX.
 			int seqVal = service.getCounterVariableByMappingId(seqCode,mappingId).getAndIncrement();

			// Preparing the pattern.
			seq = seqCode+"-" + todaysDate.format(DateTimeFormatter.ofPattern(DateUtils.SEQ_DATE_FORMAT))
			+"-"+String.format("%08d", seqVal);
		}

		return seq;
	}
	
	


}
