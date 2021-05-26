package com.ap.menabev.invoice;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.SequenceGeneratorEntity;


@Repository
public interface SequenceGeneratorRepository extends JpaRepository<SequenceGeneratorEntity, String> {

	List<SequenceGeneratorEntity> findBySeqCode(String invoiceSeqPrefix);
	
	List<SequenceGeneratorEntity> findBySeqCodeAndMappedId(String invoiceSeqPrefix,String mappedId);
	
	
	SequenceGeneratorEntity findBySeqCodeAndDate(String invoiceSeqPrefix, LocalDate date);
	
	SequenceGeneratorEntity findBySeqCodeAndDateAndMappedId(String invoiceSeqPrefix, LocalDate date,String mappedId);
	
	List<SequenceGeneratorEntity> findByDate(LocalDate date);
	
	List<SequenceGeneratorEntity> findByDateAndMappedId(LocalDate date,String mappedId);
	
}
