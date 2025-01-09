package com.user.us.user.controller;

import com.user.us.user.common.tools.DTOMapper;
import com.user.us.user.model.UserDTO;
import com.user.us.user.model.UserModel;
import com.user.us.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//ONLY FOR TEST NEED ALSO TO ALLOW CROSS ORIGIN ON WEB BROWSER SIDE
@CrossOrigin
@RestController
public class UserRestController {

    @Autowired
    UserService userService;

    @RequestMapping(method=RequestMethod.GET,value="/users")
    private List<UserDTO> getAllUsers() {
        List<UserDTO> uDTOList=new ArrayList<UserDTO>();
        for(UserModel uM: userService.getAllUserNoPwd()){
            uDTOList.add(DTOMapper.fromUserModelToUserDTO(uM));
        }
        return uDTOList;
    }

    @RequestMapping(method=RequestMethod.GET,value="/heroes")
    private List<UserDTO> getAllheroes() {
        List<UserDTO> uDTOList=new ArrayList<UserDTO>();
        for(UserModel uM: userService.getAllUsers()){
            uDTOList.add(DTOMapper.fromUserModelToUserDTO(uM));
        }
        return uDTOList;
    }

    @RequestMapping(method=RequestMethod.GET,value="/user/{id}")
    private UserDTO getUser(@PathVariable Integer id) {
        Optional<UserModel> ruser;
        ruser= userService.getUserNoPwdById(id);
        if(ruser.isPresent()) {
            return DTOMapper.fromUserModelToUserDTO(ruser.get());
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User id:"+id+", not found",null);
    }

    @RequestMapping(method=RequestMethod.GET,value="/user/{login}")
    private UserDTO getUser(@PathVariable String login) {
        Optional<UserModel> ruser;
        ruser= userService.getUserNoPwdByLogin(login);
        if(ruser.isPresent()) {
            return DTOMapper.fromUserModelToUserDTO(ruser.get());
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User login:"+login+", not found",null);
    }

    @RequestMapping(method=RequestMethod.POST,value="/user")
    public UserDTO addUser(@RequestBody UserDTO user) {
        return userService.addUser(user);
    }

    @RequestMapping(method=RequestMethod.PUT,value="/user/{id}")
    public UserDTO updateUser(@RequestBody UserDTO user,@PathVariable Integer id) {
        user.setId(id);
        return userService.updateUser(user);
    }

    // Pas très sûr de cette méthode avec le setLogin
    @RequestMapping(method=RequestMethod.PUT,value="/user/{login}")
    public UserDTO updateUser(@RequestBody UserDTO user,@PathVariable String login) {
        user.setLogin(login);
        return userService.updateUser(user);
    }

    @RequestMapping(method=RequestMethod.DELETE,value="/user/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }

    @RequestMapping(method=RequestMethod.DELETE,value="/user/{login}")
    public void deleteUser(@PathVariable String login) {
        userService.deleteUser(login);
    }

}
