package ru.itmentor.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService extends UserDetailsService {

    List<User> getUsers();
    List<Role> getRoles();
    User getUserById(int id);
    void addUser(User user);
    void updateUser(int id, User user);
    void deleteUser(int id);
}
