package ru.itmentor.spring.boot_security.demo.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService extends UserDetailsService {

    List<User> getUsers();
    List<Role> getRoles();
    User getUserById(int id);
    void addUser(User user);
    void updateUser(int id, User user);
    ResponseEntity<HttpStatus> checkErrorUpdateUser(User user, BindingResult bindingResult);
    ResponseEntity<HttpStatus> checkErrorAddUser(User user, BindingResult bindingResult);
    void deleteUser(int id);
}
