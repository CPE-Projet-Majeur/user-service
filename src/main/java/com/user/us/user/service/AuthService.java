package com.user.us.user.service;

import com.user.us.user.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.user.us.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
public class AuthService implements UserDetailsService {

//    private final UserRepository userRepository;
    @Autowired
    UserRepository userRepository;

//    public AuthService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }

    public List<UserModel> getUserByLoginPwd(String login, String password) {
        List<UserModel> ulist = null;

        ulist = userRepository.findByLoginAndPassword(login, password);
        return ulist;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Objects.requireNonNull(username);
        UserModel user = userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user;
    }
}
