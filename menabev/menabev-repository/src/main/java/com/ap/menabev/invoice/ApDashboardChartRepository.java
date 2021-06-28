package com.ap.menabev.invoice;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.InvoiceHeaderDo;
import com.ap.menabev.entity.InvoiceHeaderPk;
@Repository
public interface ApDashboardChartRepository extends JpaRepository<InvoiceHeaderDo,InvoiceHeaderPk> {
	
	Date date = new Date();
	long today = date.getTime();
	
	@Query(value = "select i from InvoiceHeaderDo i where  i.request_created_at BETWEEN :fromDate AND :toDate and  "
			+ "i.compCode=:companyCode and i.currency=:currency and i.vendorId IN (:vendorId)")
	List<InvoiceHeaderDo> getDashboardChartDetailsBetween(@Param("fromDate") long fromDate,@Param("toDate") long toDate,
			@Param("companyCode") String companyCode,@Param("currency") String currency,@Param("vendorId") List<String> vendorId);
	
	
	//FOR KPI DETAILS
	
	@Query(value = "select i from InvoiceHeaderDo i where  i.request_created_at =:toDate")
	List<InvoiceHeaderDo> getTodayKPIValues(@Param("toDate") long toDate);
	
	
	@Query(value = "select i from InvoiceHeaderDo i where  i.request_created_at BETWEEN :fromDate AND :toDate and i.invoiceStatus NOT IN ('13','15')")
	List<InvoiceHeaderDo> getPOKPIValues(@Param("fromDate") long fromDate,@Param("toDate") long toDate);
	
	
	@Query(value = "select i from InvoiceHeaderDo i where  i.request_created_at BETWEEN :fromDate AND :toDate and i.invoiceType = 'PO' ")
//	@Query(value = "select i from InvoiceHeaderDo i where  i.request_created_at BETWEEN :fromDate AND :toDate")
	List<InvoiceHeaderDo> getPOBKPIValues(@Param("fromDate") long fromDate,@Param("toDate") long toDate);
	
	
	@Query(value = "select i from InvoiceHeaderDo i where  i.request_created_at BETWEEN :fromDate AND :toDate and i.invoiceType = 'NON-PO' ")
//	@Query(value = "select i from InvoiceHeaderDo i where  i.request_created_at BETWEEN :fromDate AND :toDate ")
	List<InvoiceHeaderDo> getNPOKPIValues(@Param("fromDate") long fromDate,@Param("toDate") long toDate);
	
	
	@Query(value = "select i from InvoiceHeaderDo i where  i.request_created_at BETWEEN :fromDate AND :toDate and i.dueDate > 'today' ")
	List<InvoiceHeaderDo> getOverDueKPIValues(@Param("fromDate") long fromDate,@Param("toDate") long toDate);

}
