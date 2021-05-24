package com.ap.menabev.invoice;



import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.InvoiceHeaderDo;



@Repository
public interface InvoiceHeaderRepository extends JpaRepository<InvoiceHeaderDo,String> {

	List<InvoiceHeaderDo> findByInvoiceDateBetween(String from, String to);

	
	@Query(value = "select * from APAUTOMATION.INVOICE_HEADER where LIFECYCLE_STATUS not in ('09','00') ORDER BY CREATED_AT_IN_DB DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
	List<InvoiceHeaderDo> getAllByLimit(@Param("limit") int limit, @Param("offset") int offset);

	@Query(value = "select * from APAUTOMATION.INVOICE_HEADER where LIFECYCLE_STATUS  in ('00') ORDER BY CREATED_AT_IN_DB DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
	List<InvoiceHeaderDo> getAllDraftByLimit(@Param("limit") int limit, @Param("offset") int offset);

	@Query(value = "select * from APAUTOMATION.INVOICE_HEADER where INVOICE_TYPE='INVOICE'and LIFECYCLE_STATUS  not in ('00') ORDER BY CREATED_AT_IN_DB DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
	List<InvoiceHeaderDo> getAllNonDraftByLimit(@Param("limit") int limit, @Param("offset") int offset);

	
	@Query(value = "select * from APAUTOMATION.INVOICE_HEADER where UPPER(CHANNEL_TYPE)=Upper(:channelType) ORDER BY CREATED_AT_IN_DB DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
	List<InvoiceHeaderDo> getAllEinvoiceByLimit(@Param("channelType") String channelType, @Param("limit") int limit,
			@Param("offset") int offset);

	@Query(value = " select ih,ii from InvoiceHeaderDo ih inner join InvoiceItemDo ii on ih.requestId=ii.requestId where ih.requestId=:requestId")
	List<Object[]> getAllInvoiceDetailsOnRequestId(@Param("requestId") String requestId);

	@Query(value = "select APAUTOMATION.INVOICE_HEADER_REQUEST_ID.NEXTVAL from Dummy", nativeQuery = true)
	String getRequestId();

	@Query(value = "Select 	APAUTOMATION.INVOICE_HEADER_SEQ_DRAFT.NEXTVAL FROM DUMMY", nativeQuery = true)
	public Long getDraftRequestId();

	@Query(value = "select ih from InvoiceHeaderDo ih where ih.lifecycleStatus!=:lcs ORDER BY requestId DESC")
	List<InvoiceHeaderDo> getAllInvoiceHeader(@Param("lcs") String lcs);

	@Query(value = "select ih from InvoiceHeaderDo ih where ih.requestId=:requestId")
	InvoiceHeaderDo fetchInvoiceHeader(@Param("requestId") String requestId);
	
	 @Query("select e FROM InvoiceHeaderDo e WHERE e.requestId IN (:requestIds)")  
	 List<InvoiceHeaderDo> findByRequestIdIn(@Param("requestIds")List<String> requestIds);
	 
	/*  @Query("SELECT e FROM INVOICE_HEADER e WHERE e. IN (:names)")     // 2. Spring JPA In cause using @Query
	    List<Employee> findByEmployeeNames(@Param("names")List<String> names);*/

	@Query(value = "select i from InvoiceHeaderDo i where i.requestId=:requestId")
	InvoiceHeaderDo getInvoiceHeader(@Param("requestId") String requestId);

	@Transactional(TxType.REQUIRES_NEW)
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE InvoiceHeaderDo i SET i.lifecycleStatus=:lifecycleStatus where i.requestId=:requestId")
	int updateLifecycleStatus(@Param("lifecycleStatus") String threeWayMatchingFailed,
			@Param("requestId") String requestId);

	@Transactional(TxType.REQUIRES_NEW)
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE InvoiceHeaderDo i SET i.lifecycleStatus=:lifecycleStatus,i.grossAmount=:grossAmount ,i.balance=:balance WHERE i.requestId=:requestId "
			+ "AND i.id=:id")
	int updateInvoiceHeader(@Param("lifecycleStatus") String balanceCheckFailed,
			@Param("grossAmount") String grossAmount, @Param("balance") String balance,
			@Param("requestId") String requestId, @Param("id") String id);

	@Query("select i.refDocNum from InvoiceHeaderDo i where i.requestId=:requestId")
	Long getRefDocNumByReqId(@Param("requestId") String requestId);

	@Query(value = "select count(*) FROM APAUTOMATION.INVOICE_HEADER WHERE INVOICE_DATE>=:fromDate AND INVOICE_DATE<=:endDate AND LIFECYCLE_STATUS!='09' or CLEARING_DATE IS NULL", nativeQuery = true)
	Integer getPendingInvoicesCount(@Param("fromDate") String fromDate, @Param("endDate") String endDate);

	@Query("select i.assignedTo,count(*) from InvoiceHeaderDo i where i.dueDate between :fromDate and :endDate group by i.assignedTo")
	List<Object[]> getCountOfRecordsForDueDates(@Param("fromDate") String fromDate, @Param("endDate") String endDate);

	@Query(value = "SELECT DISTINCT S.TEXT,Count(IH.LIFECYCLE_STATUS) FROM APAUTOMATION.INVOICE_HEADER AS IH JOIN APAUTOMATION.STATUS_CONFIG AS S ON IH.LIFECYCLE_STATUS=S.LIFECYCLESTATUS AND S.LANGUAGE_KEY='EN' AND IH.LIFECYCLE_STATUS NOT IN ('09') GROUP BY S.TEXT,IH.LIFECYCLE_STATUS", nativeQuery = true)
	List<Object[]> getStatusCount();

	// @Query(value=" select ih,ii from InvoiceHeaderDo ih inner join
	// InvoiceItemDo ii on ih.requestId=ii.requestId where
	// ih.requestId=:requestId")
	// List<Object[]> getAllInvoiceDetailsOnRequestId(@Param("requestId") String
	// requestId);
	@Query("select i.assignedTo,i.dueDate from InvoiceHeaderDo i where i.lifecycleStatus !='09'") // hardcoded
																									// for
																									// SAP
																									// post
																									// Success
																									// should
																									// not
																									// display
	List<Object[]> getDuedates();

	@Query(value = "SELECT * FROM APAUTOMATION.INVOICE_HEADER where INVOICE_TYPE='INVOICE' AND LIFECYCLE_STATUS NOT IN ('09','00') AND INVOICE_DATE between :fromDate and :endDate  AND DUE_DATE < :currentDate AND COMP_CODE=:compCode ORDER BY DUE_DATE DESC, INVOICE_TOTAL DESC LIMIT:limit OFFSET:offset", nativeQuery = true)
	List<InvoiceHeaderDo> getTop5AgingReport(
	@Param("fromDate") String fromDate, @Param("endDate") String endDate,
			@Param("currentDate") String currentDate,@Param("compCode") String compCode,@Param("limit") int limit, @Param("offset") int offset);

	@Query(value = "SELECT * FROM APAUTOMATION.INVOICE_HEADER where INVOICE_TYPE='INVOICE' AND INVOICE_DATE between :fromDate  and :endDate AND COMP_CODE=:compCode AND CLEARING_DATE IS NOT NULL ORDER BY CLEARING_DATE DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
	List<InvoiceHeaderDo> getTop5RecentPayments(@Param("fromDate") String fromDate, @Param("endDate") String endDate,@Param("compCode") String compCode,
			@Param("limit") int limit, @Param("offset") int offset);

	@Query(value = "select vendor_name,count(*) from APAUTOMATION.INVOICE_HEADER where LIFECYCLE_STATUS != '09' group by vendor_name", nativeQuery = true)
	List<Object[]> getPendingInvoiceCount();

	@Query(value = "select * from APAUTOMATION.INVOICE_HEADER where DUE_DATE between :fromDate and :endDate and UPPER(ASSIGNED_TO) = UPPER(:assignedTo) and LIFECYCLE_STATUS != '09' order by REQUEST_ID desc", nativeQuery = true)
	List<InvoiceHeaderDo> getAgingReportsDetails(@Param("fromDate") String fromDate, @Param("endDate") String endDate,
			@Param("assignedTo") String assignedTo);

	@Query(value = "select * from APAUTOMATION.INVOICE_HEADER where DUE_DATE < :fromDate and UPPER(ASSIGNED_TO) = UPPER(:assignedTo) and LIFECYCLE_STATUS != '09' order by REQUEST_ID desc", nativeQuery = true)
	List<InvoiceHeaderDo> getOverDueAgingReportsDetails(@Param("fromDate") String fromDate,
			@Param("assignedTo") String assignedTo);

	@Query(value = "select * from APAUTOMATION.INVOICE_HEADER where DUE_DATE > :fromDate and UPPER(ASSIGNED_TO) = UPPER(:assignedTo) and LIFECYCLE_STATUS != '09' order by REQUEST_ID desc", nativeQuery = true)
	List<InvoiceHeaderDo> getPostAgingReportsDetails(@Param("fromDate") String fromDate,
			@Param("assignedTo") String assignedTo);

	// @Query(value="UPDATE APAUTOMATION.INVOICE_HEADER SET
	// APAUTOMATION.SAP_INVOICE_NUMBER = :SAP_INVOICE_NUMBER,APAUTOMATION"
	// + ".FISCAL_YEAR = :FISCAL_YEAR WHERE
	// APAUTOMATION.REQUEST_ID=:REQUEST_ID")
	// int updateSapInvNum(@Param("REQUEST_ID") String
	// requestId,@Param("SAP_INVOICE_NUMBER") Long invNum,@Param("FISCAL_YEAR")
	// String fiscalYear);
	// @Query(value="select vendorId from from InvoiceHeaderDo where
	// requestId=:requestId")
	// String getVendorId (@Param("requestId") String requestId);

	@Query(value = "select count(*) from  APAUTOMATION.INVOICE_HEADER where COMP_CODE=:compCode and VENDOR_ID=:vendorId and CURRENCY=:currency and INVOICE_TOTAL=:total and EXT_INV_NUM=:extInvNum and INVOICE_DATE=:invoiceDate and  LIFECYCLE_STATUS NOT IN ('00')", nativeQuery = true)
	Integer checkDuplicateInvoice(@Param("compCode") String compCode, @Param("vendorId") String vendorId,
			@Param("currency") String currency, @Param("total") String total, @Param("extInvNum") String extInvNum,
			@Param("invoiceDate") String invoiceDate);

	// @Transactional(TxType.REQUIRES_NEW)
	// @Modifying(clearAutomatically = true)
	// @Query(value = "UPDATE InvoiceHeaderDo i SET
	// i.lifecycleStatus=:01,i.refDocNum=:null WHERE i.requestId=:requestId "
	// + "AND i.refDocNum=:refDocNum")
	// int updateInvoiceHeaderForDeletePO(@Param("requestId") String requestId,
	// @Param("refDocNum") Long refDocNum);
	
	@Query(value = "select rDo.sapInvoiceNumber from InvoiceHeaderDo rDo where rDo.clearingDate IS NULL and rDo.lifecycleStatus =:lifecycleStatus")
    List<Long>  getAllByclearingDate(@Param("lifecycleStatus") String lifecycleStatus);
	
	@Query(value = "select rDo.id from InvoiceHeaderDo rDo where rDo.sapInvoiceNumber=:sapInvoiceNumber")
    String getUuidUsingSAPInvoiceNo(@Param("sapInvoiceNumber") long sapInvoiceNumber);

	@Query(value = "select rDo from InvoiceHeaderDo rDo where rDo.id =:id")
	InvoiceHeaderDo getAllById(@Param("id")String id);

	
}
