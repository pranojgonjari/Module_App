// controller/AuthController.java
package com.example.module.controller;

import com.example.module.dto.JwtRequest;
import com.example.module.dto.JwtResponse;
import com.example.module.dto.SignupRequest;
import com.example.module.entity.Employee;
import com.example.module.service.EmployeeService;
import com.example.module.service.EmployeeServiceImpl; // ✅ import impl if needed
import com.example.module.security.CustomUserDetailsService;
import com.example.module.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final EmployeeService employeeService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;

    // ✅ Spring will inject EmployeeServiceImpl automatically
    public AuthController(EmployeeService employeeService,
                          AuthenticationManager authenticationManager,
                          CustomUserDetailsService customUserDetailsService,
                          JwtUtil jwtUtil) {
        this.employeeService = employeeService;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            Employee employee = employeeService.registerEmployee(request);
            return ResponseEntity.ok("Employee registered: " + employee.getEmail());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JwtRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid email or password");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Authentication error: " + e.getMessage());
        }

        UserDetails userDetails = customUserDetailsService
                .loadUserByUsername(request.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(
                token,
                request.getEmail(),
                userDetails.getAuthorities().iterator().next().getAuthority()
        ));
    }
}