package com.ap.menabev.service;

import java.util.concurrent.atomic.AtomicInteger;

public interface SequenceGeneratorService {

	String getSequenceNo(String exchangeSeqPrefix,String mappingId);

	String getSequenceNoByMappingId(String seqCode, String mappingId);

	AtomicInteger getCounterVariableByMappingId(String seqCode, String mappingId);

}
