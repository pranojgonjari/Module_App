// service/EmployeeService.java
package com.example.module.service;

import com.example.module.dto.SignupRequest;
import com.example.module.entity.Employee;

import java.util.List;

public interface EmployeeService {
    Employee registerEmployee(SignupRequest request);
    List<Employee> getAllEmployees();
}