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
        return service.checkErrorAddUser(user, bindingResult);
    }

    @PostMapping("/saveuser")
    public ResponseEntity<HttpStatus> update(@RequestBody @Valid User user, BindingResult bindingResult) {
        return service.checkErrorUpdateUser(user, bindingResult);
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