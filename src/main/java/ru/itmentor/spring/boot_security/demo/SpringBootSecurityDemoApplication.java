package ru.itmentor.spring.boot_security.demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.itmentor.spring.boot_security.demo.dao.RoleDao;
import ru.itmentor.spring.boot_security.demo.dao.UserDao;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class SpringBootSecurityDemoApplication implements CommandLineRunner {
	private final RoleDao roleDao;
	private final UserDao userDao;


	@Autowired
	public SpringBootSecurityDemoApplication(RoleDao roleDao, UserDao userDao) {
		this.roleDao = roleDao;
		this.userDao = userDao;
	}


	public static void main(String[] args) {
		SpringApplication.run(SpringBootSecurityDemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Role adminrole = new Role(1, "ROLE_ADMIN");
		Role userrole = new Role(2, "ROLE_USER");
		roleDao.save(adminrole);
		roleDao.save(userrole);

		Set<Role> admin_roles = new HashSet<>();
		admin_roles.add(adminrole);
		admin_roles.add(userrole);

		User admin = new User(1, "admin", "admin", "admin@mail.ru", 48, admin_roles);
		userDao.save(admin);

		Set<Role> user_roles = new HashSet<>();
		user_roles.add(userrole);

		User user = new User(2, "user", "user", "user@mail.ru", 22, user_roles);
		userDao.save(user);

	}
}
