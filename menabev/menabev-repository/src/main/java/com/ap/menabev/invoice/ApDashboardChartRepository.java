package com.ap.menabev.invoice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.InvoiceHeaderDo;
import com.ap.menabev.entity.InvoiceHeaderPk;
@Repository
public interface ApDashboardChartRepository extends JpaRepository<InvoiceHeaderDo,InvoiceHeaderPk> {
	
	@Query(value = "select i from InvoiceHeaderDo i where  i.request_created_at BETWEEN :fromDate AND :toDate and  "
			+ "i.compCode=:companyCode and i.currency=:currency and i.vendorId IN (:vendorId)")
	List<InvoiceHeaderDo> getDashboardChartDetailsBetween(@Param("fromDate") long fromDate,@Param("toDate") long toDate,
			@Param("companyCode") String companyCode,@Param("currency") String currency,@Param("vendorId") List<String> vendorId);

}
