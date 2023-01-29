package ru.itmentor.spring.boot_security.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmentor.spring.boot_security.demo.model.User;
import java.util.Optional;

public interface UserDao extends JpaRepository<User, Integer> {
//        Optional<User> findByName(String name);
        Optional<User> findByEmail(String name);
}
