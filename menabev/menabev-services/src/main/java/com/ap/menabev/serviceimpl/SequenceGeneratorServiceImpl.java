package com.ap.menabev.serviceimpl;


import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ap.menabev.entity.SequenceGeneratorEntity;
import com.ap.menabev.invoice.SequenceGeneratorRepository;
import com.ap.menabev.service.SequenceGeneratorService;
import com.ap.menabev.util.DateUtils;
import com.ap.menabev.util.MenabevApplicationConstant;


@Service
public class SequenceGeneratorServiceImpl implements SequenceGeneratorService{

	@Autowired
	private SequenceGeneratorRepository repository;
	
	@Autowired
	private SequenceGenerator service;
	
	
	private AtomicInteger invoiceValue = new AtomicInteger(1);

	// Counter for return sequence
	
	private volatile LocalDate lastDate = LocalDate.now(ZONE_ID);
	
	public final static ZoneId ZONE_ID = ZoneId.systemDefault();
	
	
	/**
	 * Updates the sequences to the backup table.
	 * @param InvoiceSeqPrefix
	 */
	public void updateSeqRecord(String invoiceSeqPrefix) {
		LocalDate date = LocalDate.now(SequenceGenerator.ZONE_ID);
		
		SequenceGeneratorEntity existingRecord = repository.findBySeqCodeAndDate(invoiceSeqPrefix, date);
		
		if(existingRecord != null) {
			// Increase the count by one
			existingRecord.setCounter(existingRecord.getCounter() + 1);
		} else {
			// Creating the new entry object
			existingRecord = new SequenceGeneratorEntity();
			existingRecord.setSeqCode(invoiceSeqPrefix);
			existingRecord.setCounter(1);
			existingRecord.setDate(date);
			
			// getting the junk data
			List<SequenceGeneratorEntity> junkRecord = repository.findBySeqCode(invoiceSeqPrefix);
			
			if (junkRecord.size() > 0) {
				// Removing the junk data
				repository.deleteAll(junkRecord);
			}
		}
		// Saving the new record to backup table
		repository.save(existingRecord);	
	}

	

/*	public int getDbCounter(String exchangeSeqPrefix) {
		SequenceGeneratorEntity existingRecord = repository.findBySeqCodeAndDate(exchangeSeqPrefix,
				LocalDate.now(SequenceGenerator.ZONE_ID));		
		
		if (existingRecord != null) {
			return existingRecord.getCounter();
		} 
		
		return 0;
	}*/
	
	
	/**
	 * Fetching the existing counts from backup table if application resets.
	 * @return List<SequenceGeneratorEntity>
	 */
	public List<SequenceGeneratorEntity> getDbCounter() {
		LocalDate date = LocalDate.now(SequenceGenerator.ZONE_ID);
		List<SequenceGeneratorEntity> existingRecord = repository.findByDate(date);		
		
		return existingRecord;
	}
	
	public List<SequenceGeneratorEntity> getDbCounterByMappingId(String mappingId) {
		LocalDate date = LocalDate.now(SequenceGenerator.ZONE_ID);
		List<SequenceGeneratorEntity> existingRecord = repository.findByDateAndMappedId(date, mappingId);		
		
		return existingRecord;
	}
	
	@Override
	public String getSequenceNo(String seqCode,String mappingId) {
		SequenceGenerator generator = service.getInstance();
		String seq;
	
		if(generator.isStarted) {
			generator.reAssignValue(getDbCounter());
		}	
		
		seq = generator.getNext(seqCode,mappingId);
		
		if(seq != null && !seq.isEmpty()) {
			updateSeqRecord(seqCode);		
		}
		
		return seq;
	}
	@Override
	public String getSequenceNoByMappingId(String seqCode,String mappingId) {
		SequenceGenerator generator = service.getInstance();
		String seq;
		if(generator.isStarted) {
			generator.reAssignValue(getDbCounterByMappingId(mappingId));
			seq = generator.getNext(seqCode,mappingId);
			if(seq != null && !seq.isEmpty()) {
				updateSeqRecordByMappingId(seqCode,mappingId);		
			}
			return seq;
		}else{	
		seq = getNextByMappingId(seqCode,mappingId);
		return seq;
		}
	}
	
	
	public void updateSeqRecordByMappingId(String invoiceSeqPrefix,String mappingId) {
		LocalDate date = LocalDate.now(SequenceGenerator.ZONE_ID);
		
		SequenceGeneratorEntity existingRecord = repository.findBySeqCodeAndDateAndMappedId(invoiceSeqPrefix, date, mappingId);
		
		if(existingRecord != null) {
			// Increase the count by one
			existingRecord.setCounter(existingRecord.getCounter() + 1);
		} else {
			// Creating the new entry object
			existingRecord = new SequenceGeneratorEntity();
			existingRecord.setSeqCode(invoiceSeqPrefix);
			existingRecord.setCounter(1);
			existingRecord.setDate(date);
			existingRecord.setMappedId(mappingId);
			
			// getting the junk data
			List<SequenceGeneratorEntity> junkRecord = repository.findBySeqCodeAndMappedId(invoiceSeqPrefix, mappingId);
			
			if (junkRecord.size() > 0) {
				// Removing the junk data
				repository.deleteAll(junkRecord);
			}
		}
		// Saving the new record to backup table
		repository.save(existingRecord);	
	}
	
	@Override
	public AtomicInteger getCounterVariableByMappingId(String seqCode,String mappingId) {
		LocalDate todaysDate = LocalDate.now(ZONE_ID);
		AtomicInteger counter = new AtomicInteger();
		
		SequenceGeneratorEntity sequence =  repository.findBySeqCodeAndDateAndMappedId(seqCode, todaysDate, mappingId);
		
		if(sequence!= null){

		switch (sequence.getSeqCode()) {
		case MenabevApplicationConstant.INVOICE_SEQUENCE:
			counter.set(sequence.getCounter()+1);
			break;
		default:
			break;
		}
		updateSeqRecordByMappingIdForCounter(seqCode, mappingId);
		return counter;
		}else{
			// if the mapping id is not there than create a mapping id and set counter to 1 
			  int newUpdatedMappingIdCount = updateSeqRecordByMappingIdForCounter(seqCode, mappingId);
			  
			  switch (seqCode) {
				case MenabevApplicationConstant.INVOICE_SEQUENCE:
					counter.set(newUpdatedMappingIdCount);
					break;
				default:
					break;
				}
			
			
			return counter;
		}
	}
	
	
	public int updateSeqRecordByMappingIdForCounter(String exchangeSeqPrefix,String mappingId) {
		LocalDate date = LocalDate.now(SequenceGenerator.ZONE_ID);
		
		SequenceGeneratorEntity existingRecord = repository.findBySeqCodeAndDateAndMappedId(exchangeSeqPrefix, date, mappingId);
		
		if(existingRecord != null) {
			// Increase the count by one
			existingRecord.setCounter(existingRecord.getCounter() + 1);
		} else {
			// Creating the new entry object
			existingRecord = new SequenceGeneratorEntity();
			existingRecord.setSeqCode(exchangeSeqPrefix);
			existingRecord.setCounter(1);
			existingRecord.setDate(date);
			existingRecord.setMappedId(mappingId);
			
			// getting the junk data
			List<SequenceGeneratorEntity> junkRecord = repository.findBySeqCodeAndMappedId(exchangeSeqPrefix, mappingId);
			
			if (junkRecord.size() > 0) {
				// Removing the junk data
				repository.deleteAll(junkRecord);
			}
		}
		// Saving the new record to backup table
		return repository.save(existingRecord).getCounter();	
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
 			int seqVal = getCounterVariableByMappingId(seqCode,mappingId).getAndIncrement();

			// Preparing the pattern.
			seq = seqCode+"-" + todaysDate.format(DateTimeFormatter.ofPattern(DateUtils.SEQ_DATE_FORMAT))
					+"-"+String.format("%08d", seqVal);
		}

		return seq;
	}
	
	private void resetAll() {
		invoiceValue.set(1);
		
		
	}
	
	

	
	
}
