package com.ap.menabev.service;

import java.util.List;

import com.ap.menabev.entity.EmployeeEntity;
import com.ap.menabev.util.RecordNotFoundException;

public interface EmployeeService {
	public List<EmployeeEntity> getAllEmployees();

	public EmployeeEntity getEmployeeById(Long id) throws RecordNotFoundException;

	public EmployeeEntity createOrUpdateEmployee(EmployeeEntity entity) throws RecordNotFoundException;

	public void deleteEmployeeById(Long id) throws RecordNotFoundException;
}
