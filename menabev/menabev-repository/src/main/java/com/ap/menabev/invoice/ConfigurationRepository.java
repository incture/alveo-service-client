package com.ap.menabev.invoice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.ConfigurationDo;

@Repository
public interface ConfigurationRepository extends JpaRepository<ConfigurationDo, String> {
	@Query("select cDo from ConfigurationDo cDo where   upper(cDo.version)=upper(:version)")
	public ConfigurationDo getVersion(@Param("version") String version);
	@Query(value = "select CONFIGURATION_VERSION.NEXTVAL from Dummy", nativeQuery = true)
	String getVersionNum();
	@Query("select cDo.version from ConfigurationDo cDo")
	public List<String> getVersion();

	
  
}

