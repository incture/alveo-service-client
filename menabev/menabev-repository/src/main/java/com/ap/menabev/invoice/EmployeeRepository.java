package com.ap.menabev.invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.EmployeeEntity;

@Repository
public interface EmployeeRepository
        extends JpaRepository<EmployeeEntity, Long> {
 
}