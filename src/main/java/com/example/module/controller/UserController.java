package com.example.module.controller;


import com.example.module.entity.Users;
import com.example.module.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/user")
public class  UserController {

    @Autowired
    private UserService userService;


    @GetMapping
    public List<Users> getallUsers(){
        return userService.getAllEntries();
    }

    @PostMapping
    public void createUser(@RequestBody Users user){
        userService.saveEntry(user);
    }

    @PutMapping("/{username}")
    public ResponseEntity<?> update(@RequestBody Users user,@PathVariable String username){

        Users userInDb = userService.findByUsername(username);

        if(userInDb != null){

            userInDb.setUsername(user.getUsername());
            userInDb.setPassword(user.getPassword());

            userService.saveEntry(userInDb);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
