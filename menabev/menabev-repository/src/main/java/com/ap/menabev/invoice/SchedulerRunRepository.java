package com.ap.menabev.invoice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.SchedulerRunDo;

@Repository
public interface SchedulerRunRepository extends JpaRepository<SchedulerRunDo, String> {
	@Query("update SchedulerRunDo set DatetimeSwitchedOFF=:time ")
	int update();
	@Query("select scr from SchedulerRunDo scr where schedulerConfigID=:scId")
	SchedulerRunDo getBySchedulerConfigID(@Param("scId") String scId);
	@Query("select scr from SchedulerRunDo scr where schedulerConfigID in (:scId)")
	List<SchedulerRunDo> findAllByScId(@Param("scId") List<String> scID);

}
