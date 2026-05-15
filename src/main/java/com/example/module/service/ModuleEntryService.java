package com.example.module.service;

import com.example.module.entity.ModuleEntry;
import com.example.module.entity.Users;
import com.example.module.repository.ModuleEntryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ModuleEntryService {

    @Autowired
    private ModuleEntryRepository moduleEntryRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public ModuleEntry saveEntry(ModuleEntry moduleEntry, String username) {

        try {

            Users user = userService.findByUsername(username);

            if (user == null) {
                throw new RuntimeException("User not found");
            }

            moduleEntry.setDate(LocalDateTime.now());

            ModuleEntry saved = moduleEntryRepository.save(moduleEntry);

            user.getModuleEntries().add(saved);

            userService.saveEntry(user);

            return saved;

        } catch (Exception e) {

            System.out.println("Exception occurred: " + e.getMessage());

            throw new RuntimeException("Failed to save entry");
        }
    }

    public List<ModuleEntry> getAll() {
        return moduleEntryRepository.findAll();
    }

    public Optional<ModuleEntry> findById(ObjectId id) {
        return moduleEntryRepository.findById(id);
    }

    public void deleteByID(ObjectId id) {
        moduleEntryRepository.deleteById(id);
    }
}