package org.example.shareit.User;

import lombok.extern.slf4j.Slf4j;
import org.example.shareit.User.Exceptions.NonExistingUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        List<User> users = new ArrayList<User>();
        try{
            users=userService.findAllUsers();
            log.info("Found "+users.size()+" users");
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
        catch(Exception e){
            log.error("Cannot get users list: "+e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<?> addUser(@RequestBody @Validated User user) {
        try{
            User createdUser=userService.createUser(user);
            log.info("User created");
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        }
        catch(Exception e)
        {
            log.error("Error creating user",e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PatchMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(@RequestBody @Validated User user, @PathVariable int userId) {
        try{
            User updatedUser=userService.updateUser(user,userId);
            log.info("User with id="+userId+" updated");
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }
        catch(NonExistingUserException e)
        {
            log.error("User with id="+userId+" not found");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable int id) {}

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUser(@PathVariable int userId) {
        try{
            User user=userService.findUserById(userId);
            log.info("User with id="+userId+" found");
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        catch(NonExistingUserException e)
        {
            log.error("User with id="+userId+" not found");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
