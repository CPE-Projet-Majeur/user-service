package com.user.us.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.user.us.user.common.tools.DTOMapper;
import com.user.us.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import com.user.us.user.model.UserDTO;
import com.user.us.user.model.UserModel;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserModel> getAllUsers() {
        List<UserModel> userList = new ArrayList<>();
        userRepository.findAll().forEach(userList::add);
        return userList;
    }

    public Optional<UserModel> getUser(String id) {
        return userRepository.findById(Integer.valueOf(id));
    }

    public Optional<UserModel> getUser(Integer id) {
        return userRepository.findById(id);
    }

    public UserDTO addUser(UserDTO user) {
        UserModel u = fromUDtoToUModel(user);
        // needed to avoid detached entity passed to persist error
        UserModel u_saved=userRepository.save(u);

        UserModel uBd = userRepository.save(u_saved);
        return DTOMapper.fromUserModelToUserDTO(uBd);
    }

    @Transactional
    public UserDTO updateUser(UserDTO user) {
        UserModel u = fromUDtoToUModel(user);
        UserModel uBd =userRepository.save(u);
        return DTOMapper.fromUserModelToUserDTO(uBd);
    }

    @Transactional
    public UserDTO updateUser(UserModel user) {
        UserModel uBd = userRepository.save(user);
        return DTOMapper.fromUserModelToUserDTO(uBd);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(Integer.valueOf(id));
    }

    private UserModel fromUDtoToUModel(UserDTO user) {
        UserModel u = new UserModel(user);
        return u;
    }

}
