package com.bistroops.employee.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<EmployeeVO, Integer> {
	// 主鍵就是 emp_no，用內建的 findById(empNo) 即可
}
