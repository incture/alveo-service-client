package com.ap.menabev.invoice;

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

}
