package com.ap.menabev.invoice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.SchedulerConfigurationDo;


@Repository
public interface SchedulerConfigurationRepository extends JpaRepository<SchedulerConfigurationDo, String> {
	@Query("select sDo from SchedulerConfigurationDo sDo where sDo.configurationId=:configurationId")
	public List<SchedulerConfigurationDo> getSchedulerConfiguration(@Param("configurationId") String configurationId);
}
