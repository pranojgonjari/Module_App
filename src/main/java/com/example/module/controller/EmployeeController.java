// controller/EmployeeController.java
package com.example.module.controller;

import com.example.module.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/abc")
public class EmployeeController {

    private final EmployeeService employeeService; // ✅ inject service

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/empdata")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello! JWT is working.");
    }

    @GetMapping("/employees")
    public ResponseEntity<?> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees()); // ✅ return from DB
    }
}