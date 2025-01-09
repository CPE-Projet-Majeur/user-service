package com.user.us.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.user.us.user.common.tools.DTOMapper;
import com.user.us.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.us.user.model.UserDTO;
import com.user.us.user.model.UserModel;
import org.springframework.transaction.annotation.Transactional;


// Ne pas renvoyer le pwd lors des get ou put ? (meme si encrypted)
@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public List<UserModel> getAllUsers() {
        List<UserModel> userList = new ArrayList<>();
        userRepository.findAll().forEach(userList::add);
        return userList;
    }

    public List<UserModel> getAllUserNoPwd() {
        List<UserModel> userList = new ArrayList<>();
        userRepository.findAll().forEach(s -> {
            s.setPwd("*************");
            userList.add(s);
        });
        return userList;
    }

    public Optional<UserModel> getUser(String id) {
        return userRepository.findById(Integer.valueOf(id));
    }

    public Optional<UserModel> getUser(Integer id) {
        return userRepository.findById(id);
    }

    public Optional<UserModel> getUserByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public Optional<UserModel> getUserNoPwdByLogin(String login) {
        Optional<UserModel> uOpt =userRepository.findByLogin(login);
        if( uOpt.isPresent()){
            UserModel u=uOpt.get();
            u.setPwd("*************");
            return Optional.of(u);
        }
        return null;
    }

    public Optional<UserModel> getUserNoPwdById(Integer id) {
        Optional<UserModel> uOpt =userRepository.findById(id);
        if( uOpt.isPresent()){
            UserModel u=uOpt.get();
            u.setPwd("*************");
            return Optional.of(u);
        }
        return null;
    }



    public UserDTO addUser(UserDTO user) {
        UserModel u = fromUDtoToUModel(user);
        // Encodage du password
        u.setPwd(passwordEncoder.encode(u.getPwd()));
        // needed to avoid detached entity passed to persist error
        UserModel u_saved=userRepository.save(u);

        UserModel uBd = userRepository.save(u_saved);
        return DTOMapper.fromUserModelToUserDTO(uBd);
    }

    @Transactional
    public UserDTO updateUser(UserDTO user) {
        UserModel u = fromUDtoToUModel(user);
        // Encodage du pwd
        u.setPwd(passwordEncoder.encode(u.getPwd()));
        UserModel uBd =userRepository.save(u);
        return DTOMapper.fromUserModelToUserDTO(uBd);
    }

    @Transactional
    public UserDTO updateUser(UserModel user) {
        UserModel uBd = userRepository.save(user);
        uBd.setPwd(passwordEncoder.encode(uBd.getPwd()));

        return DTOMapper.fromUserModelToUserDTO(uBd);
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
    public void deleteUser(String login) {
        userRepository.deleteByLogin(login);
    }


    private UserModel fromUDtoToUModel(UserDTO user) {
        UserModel u = new UserModel(user);
        return u;
    }

}
