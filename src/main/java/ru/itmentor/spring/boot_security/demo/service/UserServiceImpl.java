package ru.itmentor.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.dao.RoleDao;
import ru.itmentor.spring.boot_security.demo.dao.UserDao;
import ru.itmentor.spring.boot_security.demo.util.UserNotCreatedException;
import ru.itmentor.spring.boot_security.demo.util.UserNotFoundException;


import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final RoleDao roleDao;

    @Autowired
    public UserServiceImpl(UserDao userDao, RoleDao roleDao) {
        this.userDao = userDao;
        this.roleDao = roleDao;
    }

    @Override
    @Transactional
    public void addUser(User user) {
        userDao.save(user);
    }

    @Override
    @Transactional
    public void updateUser(int id, User user) {
        User updatedUser = userDao.getById(id);
        updatedUser.setName(user.getName());
        updatedUser.setEmail(user.getEmail());
        updatedUser.setAge(user.getAge());
        updatedUser.setPassword(user.getPassword());
        updatedUser.setRoles(user.getRoles());
        userDao.save(updatedUser);
    }

    @Override
    public void deleteUser(int id) {
        userDao.deleteById(id);
    }

    @Override
    public User getUserById(int id) {
        Optional<User> founduser = userDao.findById(id);
        return founduser.orElseThrow(UserNotFoundException::new);
    }

    @Override
    public List<User> getUsers() {
        return userDao.findAll();
    }

    @Override
    public List<Role> getRoles() {
        return roleDao.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Optional<User> user = userDao.findByEmail(name);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return user.get();
    }
    @Override
    public ResponseEntity<HttpStatus> checkErrorUpdateUser(User user, BindingResult bindingResult){
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
            updateUser(user.getId(), user);
            return ResponseEntity.ok(HttpStatus.OK);
        }
    };

    @Override
    public ResponseEntity<HttpStatus> checkErrorAddUser(User user, BindingResult bindingResult){
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
            addUser(newuser);
            newuser.setRoles(user.getRoles());
            addUser(newuser);
            return ResponseEntity.ok(HttpStatus.OK);
        }
    };

}
