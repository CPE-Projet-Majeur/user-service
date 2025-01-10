package com.user.us.user.service;

import com.user.us.user.model.UserModel;
import org.springframework.stereotype.Service;
import com.user.us.user.repository.UserRepository;

import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserModel> getUserByLoginPwd(String login, String password) {
        List<UserModel> ulist = null;
        ulist = userRepository.findByLoginAndPassword(login, password);
        return ulist;
    }
}
