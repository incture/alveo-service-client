package com.ap.menabev.invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.SchedulerRunDo;

@Repository
public interface SchedulerRunRepository extends JpaRepository<SchedulerRunDo, String> {

}
