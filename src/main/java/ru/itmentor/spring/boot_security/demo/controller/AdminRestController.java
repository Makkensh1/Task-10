package ru.itmentor.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.service.UserService;
import ru.itmentor.spring.boot_security.demo.util.UserErrorResponse;
import ru.itmentor.spring.boot_security.demo.util.UserNotCreatedException;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/admin")
public class AdminRestController {
    private final UserService service;

    @Autowired
    public AdminRestController(UserService service) {
        this.service = service;

    }

    @GetMapping("/table")
    public List<User> findAll() {
        return service.getUsers();
    }

    @GetMapping("/found/{id}")
    public User getUser(@PathVariable("id") int id) {
        User user = service.getUserById(id);
        return user;
    }

    @DeleteMapping("/delete/{id}")
    public int deleteUser(@PathVariable("id") int id) {
        service.deleteUser(id);
        return id;
    }


    @PostMapping("/new")
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid User user, BindingResult bindingResult) {
        User newuser = new User();
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append("-")
                        .append(error.getDefaultMessage())
                        .append(";");
            }
            throw new UserNotCreatedException(errorMsg.toString());
        } else {
            newuser.setName(user.getName());
            newuser.setAge(user.getAge());
            newuser.setEmail(user.getEmail());
            newuser.setPassword(user.getPassword());
            service.addUser(newuser);
            newuser.setRoles(user.getRoles());
            service.addUser(newuser);
            return ResponseEntity.ok(HttpStatus.OK);
        }
    }

    @PostMapping("/saveuser")
    public ResponseEntity<HttpStatus> update(@RequestBody @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append("-")
                        .append(error.getDefaultMessage())
                        .append(";");
            }
            throw new UserNotCreatedException(errorMsg.toString());
        } else {
            service.updateUser(user.getId(), user);
            return ResponseEntity.ok(HttpStatus.OK);
        }
    }


    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UsernameNotFoundException e) {
        UserErrorResponse userErrorResponse = new UserErrorResponse("User with this id not found!", System.currentTimeMillis());
        return new ResponseEntity<>(userErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotCreatedException e) {
        UserErrorResponse userErrorResponse = new UserErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(userErrorResponse, HttpStatus.BAD_REQUEST);
    }
}