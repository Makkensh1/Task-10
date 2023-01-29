package ru.itmentor.spring.boot_security.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.service.UserService;

@Component
public class AuthProviderImpl implements AuthenticationProvider {
    private final UserService userService;

    @Autowired
    public AuthProviderImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        UserDetails userdetails = userService.loadUserByUsername(name);
        String password = authentication.getCredentials().toString();
        if (!password.equals(userdetails.getPassword())) {
            throw new BadCredentialsException("Incorrect password !");
        }
        return new UsernamePasswordAuthenticationToken(userdetails, password, ((User) userdetails).getRoles());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
