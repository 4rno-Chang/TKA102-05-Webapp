package com.bistroops.security.admin;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.bistroops.employee.model.EmployeeVO;

// 登入成功後存在 Session 裡的「登入者資料」
public class AdminUserDetails implements UserDetails {

	private final Integer empNo;
	private final String empName;
	private final String password;
	private final boolean enabled;
	private final Collection<? extends GrantedAuthority> authorities;

	public AdminUserDetails(EmployeeVO emp, Collection<? extends GrantedAuthority> authorities) {
		this.empNo = emp.getEmpNo();
		this.empName = emp.getEmpName();
		this.password = emp.getEmpPassword();
		this.enabled = "在職".equals(emp.getEmpStatus()); // 離職員工不能登入
		this.authorities = authorities;
	}

	// 擴充方法：讓頁面可以顯示登入者
	public Integer getEmpNo() {
		return empNo;
	}

	public String getEmpName() {
		return empName;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return String.valueOf(empNo); // 登入帳號就是員工編號
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
}
