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

    public List<UserModel> getUserByLoginPwd(String login, String pwd) {
        List<UserModel> ulist = null;
        ulist = userRepository.findByLoginAndPwd(login, pwd);
        return ulist;
    }
}
