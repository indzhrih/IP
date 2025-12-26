package com.example.library.service;

import com.example.library.model.AppUser;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class XmlUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public XmlUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        String roleName = "ROLE_" + appUser.getRole().name();
        return new User(
                appUser.getUsername(),
                appUser.getPassword(),
                AuthorityUtils.createAuthorityList(roleName)
        );
    }
}
