package com.example.module.controller;

import com.example.module.entity.ModuleEntry;
import com.example.module.service.ModuleEntryService;
import com.example.module.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.module.entity.Users;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/module")
public class ModuleEntryControllerV2 {

    @Autowired
    private ModuleEntryService moduleEntryService;

    @Autowired
    private UserService userService;


    @GetMapping
    public ResponseEntity<List<ModuleEntry>> getAllEntries() {

        List<ModuleEntry> all = moduleEntryService.getAll();

        if (!all.isEmpty()) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getAllModuleEntriesOfUser(@PathVariable String username) {

        Users user = userService.findByUsername(username);

        List<ModuleEntry> all = user.getModuleEntries();

        if (all != null && !all.isEmpty()) {

            return new ResponseEntity<>(all, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PostMapping("/{username}")
    public ResponseEntity<?> createEntryOfUser(
            @PathVariable String username,
            @RequestBody ModuleEntry myEntry) {

        try {

            ModuleEntry saved = moduleEntryService.saveEntry(myEntry, username);

            return new ResponseEntity<>(saved, HttpStatus.CREATED);

        } catch (Exception e) {

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }



    @GetMapping("id/{myId}")
    public ResponseEntity<ModuleEntry> getmoduleEntryById(@PathVariable ObjectId myId) {

        Optional<ModuleEntry> moduleEntry = moduleEntryService.findById(myId);
        if (moduleEntry.isPresent()) {
            return new ResponseEntity<>(moduleEntry.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("id/{myId}")
    public ResponseEntity<?> deletemoduleEntryById(@PathVariable ObjectId myId) {
        moduleEntryService.deleteByID(myId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/id/{username}/{id}")
    public ResponseEntity<?> updatemoduleEntryById(
            @PathVariable String username,
            @PathVariable ObjectId id,
            @RequestBody ModuleEntry newEntry) {

        ModuleEntry old = moduleEntryService.findById(id).orElse(null);

        if (old != null) {

            old.setTitle(
                    newEntry.getTitle() != null &&
                            !newEntry.getTitle().equals("")
                            ? newEntry.getTitle()
                            : old.getTitle());

            old.setContent(
                    newEntry.getContent() != null &&
                            !newEntry.getContent().equals("")
                            ? newEntry.getContent()
                            : old.getContent());

            moduleEntryService.saveEntry(old, username);

            return new ResponseEntity<>(old, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
