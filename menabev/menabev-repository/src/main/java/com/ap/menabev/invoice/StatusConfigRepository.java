package com.ap.menabev.invoice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.StatusConfigDo;


@Repository
public interface StatusConfigRepository extends JpaRepository<StatusConfigDo, Integer> {


	@Query(value = "Select sDo from StatusConfigDo sDo where sDo.langKey=:langKey")
	List<StatusConfigDo>getStatusByLangKey(@Param("langKey") String langKey);
	
	@Query(value = "Select text from StatusConfigDo where lifeCycleStatus=:lifeCycleStatus and langKey=:langKey")
	String text(@Param("lifeCycleStatus") String lifeCycleStatus ,@Param("langKey") String langKey);
	
	@Query(value = "Select text from StatusConfigDo where lifeCycleStatus=:lifeCycleStatus")
	String getTextBylifeCycleStatus(@Param("lifeCycleStatus") String lifeCycleStatus);
}
