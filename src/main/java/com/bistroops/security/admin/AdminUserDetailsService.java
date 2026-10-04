package com.bistroops.security.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bistroops.employee.model.EmployeeRepository;
import com.bistroops.employee.model.EmployeeVO;

// 後台員工登入驗證：用員工編號查出員工與權限
@Service
public class AdminUserDetailsService implements UserDetailsService {

	private final EmployeeRepository employeeRepository;

	public AdminUserDetailsService(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	@Override
	@Transactional(readOnly = true) // 讀取 LAZY 的權限集合需要交易範圍
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		// 1. 表單送來的是字串，轉成員工編號
		Integer empNo;
		try {
			empNo = Integer.valueOf(username);
		} catch (NumberFormatException e) {
			throw new UsernameNotFoundException("員工編號格式錯誤：" + username);
		}

		// 2. 查詢員工
		EmployeeVO emp = employeeRepository.findById(empNo)
				.orElseThrow(() -> new UsernameNotFoundException("員工編號 [" + empNo + "] 不存在"));

		// 3. 權限轉成 GrantedAuthority，例如 perm_no=5 → "PERM_5"
		List<GrantedAuthority> authorities = emp.getEmployeepermissions().stream()
				.map(ep -> new SimpleGrantedAuthority("PERM_" + ep.getPermission().getPermNo()))
				.collect(Collectors.toList());

		return new AdminUserDetails(emp, authorities);
	}
}
