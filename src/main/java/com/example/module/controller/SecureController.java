package com.example.module.controller;

import com.example.module.entity.ModuleEntry;
import com.example.module.service.ModuleEntryService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SecureController {

    @Autowired
    private ModuleEntryService moduleEntryService;

    // --- SECURED USER ENDPOINTS ---

    @GetMapping("/hello")
    public String secureHello(Authentication authentication) {
        return "Hello " + authentication.getName() + "! This is a secured user endpoint.";
    }

    @GetMapping("/user-info")
    public UserInfoResponse getUserInfo(Authentication authentication) {
        String roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));

        return new UserInfoResponse(
                authentication.getName(),
                roles,
                authentication.isAuthenticated()
        );
    }

    @GetMapping("/entries")
    public ResponseEntity<?> getAllEntries(Authentication authentication) {
        return new ResponseEntity<>(moduleEntryService.getAll(), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ModuleEntry entry, Authentication authentication) {
        ModuleEntry saved = moduleEntryService.saveEntry(entry, authentication.getName());
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable ObjectId id, @RequestBody ModuleEntry entry, Authentication authentication) {
        Optional<ModuleEntry> old = moduleEntryService.findById(id);
        if (old.isPresent()) {
            entry.setId(id);
            ModuleEntry updated = moduleEntryService.saveEntry(entry, authentication.getName());
            return new ResponseEntity<>(updated, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable ObjectId id) {
        moduleEntryService.deleteByID(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --- ADMIN ONLY ENDPOINTS ---

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint(Authentication authentication) {
        return "Welcome Admin " + authentication.getName() + "! This is an admin-only endpoint.";
    }

    @PostMapping("/admin/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminCreate(@RequestBody ModuleEntry entry, Authentication authentication) {
        ModuleEntry saved = moduleEntryService.saveEntry(entry, authentication.getName());
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/admin/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminUpdate(@PathVariable ObjectId id, @RequestBody ModuleEntry entry, Authentication authentication) {
        Optional<ModuleEntry> old = moduleEntryService.findById(id);
        if (old.isPresent()) {
            entry.setId(id);
            ModuleEntry updated = moduleEntryService.saveEntry(entry, authentication.getName());
            return new ResponseEntity<>(updated, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/admin/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminDelete(@PathVariable ObjectId id) {
        moduleEntryService.deleteByID(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public static class UserInfoResponse {
        public String username;
        public String roles;
        public boolean authenticated;

        public UserInfoResponse(String username, String roles, boolean authenticated) {
            this.username = username;
            this.roles = roles;
            this.authenticated = authenticated;
        }

        public String getUsername() { return username; }
        public String getRoles() { return roles; }
        public boolean isAuthenticated() { return authenticated; }
    }
}

