package com.ap.menabev.invoice;

import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.SchedulerConfigurationDo;

@Repository
public interface SchedulerConfigurationRepository extends JpaRepository<SchedulerConfigurationDo, String> {
	@Query("select sDo from SchedulerConfigurationDo sDo where sDo.configurationId=:configurationId")
	public List<SchedulerConfigurationDo> getSchedulerConfiguration(@Param("configurationId") String configurationId);

	@Query(value = "select to_varchar(current_timestamp) as t from dummy", nativeQuery = true)
	public String getCurrentServerDate();

	@Query("select sc from SchedulerConfigurationDo as sc join ConfigurationDo as c on sc.configurationId = c.configurationId "
			+ " where c.version='CURRENT' and sc.actionType=:actionType and to_timestamp(sc.endDate)>to_timestamp(:currentTime) and sc.isActive=true")
	public SchedulerConfigurationDo getSchedulerData(@Param("actionType") String actionType,
			@Param("currentTime") String currentTime);

	@Transactional(TxType.REQUIRES_NEW)
	@Modifying(clearAutomatically = true)
	@Query("update SchedulerConfigurationDo set isActive=false where configurationId!=:configId and scId=:scId")
	public Integer resetFlagIsActive(@Param("configId") String configId, @Param("scId") String scId);

	@Query("select sc from SchedulerConfigurationDo as sc join ConfigurationDo as c on sc.configurationId = c.configurationId "
			+ " where c.version='CURRENT' and sc.actionType in (:actionType) and to_timestamp(sc.endDate)>to_timestamp(:currentTime) and sc.isActive=true")
	public SchedulerConfigurationDo getSchedulerFilter(@Param("actionType") List<String> actionType,
			@Param("currentTime") String currentTime);

	@Query(value = "select  sc.* from SCHEDULER_CONFIGURATION as sc join CONFIGURATION as c on sc.CONFIGURATION_ID=c.CONFIGURATION_ID where upper(c.VERSION)=upper(:version)", nativeQuery = true)
	public List<SchedulerConfigurationDo> getCurrentScheduler(@Param("version") String version);

}
