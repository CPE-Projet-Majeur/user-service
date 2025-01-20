package com.user.us.user.controller;

import com.user.us.user.common.tools.DTOMapper;
import com.user.us.user.common.tools.JwtTokenUtil;
import com.user.us.user.model.LoginResponse;
import com.user.us.user.model.RoleDTO;
import com.user.us.user.model.UserDTO;
import com.user.us.user.model.UserModel;
import com.user.us.user.service.AuthService;
import com.user.us.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//ONLY FOR TEST NEED ALSO TO ALLOW CROSS ORIGIN ON WEB BROWSER SIDE
@CrossOrigin
@RestController
public class UserRestController {

    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @RequestMapping(method=RequestMethod.GET,value="/users")
    private List<UserDTO> getAllUsers() {
        List<UserDTO> uDTOList=new ArrayList<UserDTO>();
        for(UserModel uM: userService.getAllUserNoPwd()){
            uDTOList.add(DTOMapper.fromUserModelToUserDTO(uM));
        }
        return uDTOList;
    }

    // TODO : delete heroes
    @RequestMapping(method=RequestMethod.GET,value="/heroes")
    private List<UserDTO> getAllheroes() {
        List<UserDTO> uDTOList=new ArrayList<UserDTO>();
        for(UserModel uM: userService.getAllUsers()){
            uDTOList.add(DTOMapper.fromUserModelToUserDTO(uM));
        }
        return uDTOList;
    }

    @RequestMapping(method=RequestMethod.GET,value="/heroes2")
    private List<UserDTO> getAllheroes2() {
        List<UserDTO> uDTOList=new ArrayList<UserDTO>();
        for(UserModel uM: userService.getAllUsers()){
            uDTOList.add(DTOMapper.fromUserModelToUserDTO(uM));
        }
        return uDTOList;
    }


    @RequestMapping(method=RequestMethod.GET,value="/users/{id}")
    private UserDTO getUser(@PathVariable String id) {
        Optional<UserModel> ruser;
        ruser= userService.getUserNoPwdById(Integer.valueOf(id));
        if(ruser.isPresent()) {
            return DTOMapper.fromUserModelToUserDTO(ruser.get());
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User id:"+id+", not found",null);
    }

    @RequestMapping(method=RequestMethod.GET,value="/users/login/{login}")
    private UserDTO getUserByLogin(@PathVariable String login) {
        Optional<UserModel> ruser;
        ruser = userService.getUserNoPwdByLogin(login);
        if(ruser.isPresent()) {
            return DTOMapper.fromUserModelToUserDTO(ruser.get());
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User login"+login+", not found",null);
    }

    //TODO : voir si transactional sur une fonction qui fait le add et le token ou laisser comme ça 
    @RequestMapping(method=RequestMethod.POST,value="/admin/user")
    public UserDTO addUserAsAdmin(@RequestBody UserDTO user) {
        System.out.println(user);
        UserDTO userSaved = userService.addUserAsAdmin(user);
        return userSaved;
    }

    @RequestMapping(method=RequestMethod.POST,value="/users")
    public ResponseEntity<Map<String, String>> addUserGiveToken(@RequestBody UserDTO user) {
        System.out.println(user);
        return userService.addUserGiveToken(user); // Token et user envoyé
    }

    @RequestMapping(method=RequestMethod.PUT,value="/users/{id}")
    public UserDTO updateUser(@RequestHeader("Authorization") String token, @RequestBody UserDTO user, @PathVariable String id) {
        user.setId(Integer.valueOf(id));
        return userService.updateUser(user, token);
    }

    @RequestMapping(method=RequestMethod.PUT,value="/admin/user/{id}")
    public UserDTO updateUserAsAdmin(@RequestBody UserDTO user,@PathVariable String id) {
        user.setId(Integer.valueOf(id));
        return userService.updateUserAsAdmin(user);
    }

    @RequestMapping(method=RequestMethod.PUT,value="/admin/user/{id}/role")
    public UserDTO addRoleToUser(@RequestBody RoleDTO roles, @PathVariable String id) {
        return userService.addRoleToUser(roles, Integer.valueOf(id));
    }

    @RequestMapping(method=RequestMethod.DELETE,value="/users/{id}")
    public void deleteUser(@RequestHeader("Authorization") String token, @PathVariable String id) {
        userService.deleteUser(Integer.valueOf(id), token);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/admin/roles")
    public ResponseEntity<Map<String, List<String>>> addRole(@RequestBody RoleDTO roles) {
        Map<String, List<String>> result = userService.addRoles(roles);

        return ResponseEntity.ok(result);
    }

}
