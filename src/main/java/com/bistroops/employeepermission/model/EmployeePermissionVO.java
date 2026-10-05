package com.bistroops.employeepermission.model;

import com.bistroops.employee.model.EmployeeVO;
import com.bistroops.permission.model.PermissionVO;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
@Entity
@Table(name = "emp_permission")
public class EmployeePermissionVO implements java.io.Serializable {
	
	@EmbeddedId
	private EmployeePermissionId ids;
	
	@MapsId("empNo")
	@ManyToOne
	@JoinColumn(name = "emp_no",referencedColumnName = "emp_no")
	private EmployeeVO employee;
	
	@MapsId("permNo")
	@ManyToOne
	@JoinColumn(name = "perm_no", referencedColumnName = "perm_no")
	private PermissionVO permission;

	public EmployeePermissionVO() {
	}

	public EmployeeVO getEmployee() {
		return employee;
	}

	public void setEmployee(EmployeeVO employee) {
		this.employee = employee;
	}

	public PermissionVO getPermission() {
		return permission;
	}

	public void setPermission(PermissionVO permission) {
		this.permission = permission;
	}

		@Override
	public String toString() {
		return "EmployeePermissionVO [empNo=" + employee.getEmpNo() + ", permNo=" + permission.getPermNo() + "]";
	}


	

}
