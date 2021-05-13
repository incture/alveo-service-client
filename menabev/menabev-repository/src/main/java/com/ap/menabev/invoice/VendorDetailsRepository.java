package com.ap.menabev.invoice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.VendorDetailsDo;
@Repository
public interface VendorDetailsRepository extends JpaRepository<VendorDetailsDo, String> {
	@Query("select vDo from VendorDetailsDo vDo where vDo.configurationId=:configurationId")
	public List<VendorDetailsDo> getVendorDetails(@Param("configurationId") String configurationId);
}
