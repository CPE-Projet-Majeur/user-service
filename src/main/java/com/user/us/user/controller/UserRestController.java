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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
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

    @RequestMapping(method=RequestMethod.GET,value="/heroes")
    private List<UserDTO> getAllheroes() {
        List<UserDTO> uDTOList=new ArrayList<UserDTO>();
        for(UserModel uM: userService.getAllUsers()){
            uDTOList.add(DTOMapper.fromUserModelToUserDTO(uM));
        }
        return uDTOList;
    }

    @RequestMapping(method=RequestMethod.GET,value="/user/{id}")
    private UserDTO getUser(@PathVariable String id) {
        Optional<UserModel> ruser;
        ruser= userService.getUserNoPwdById(Integer.valueOf(id));
        if(ruser.isPresent()) {
            return DTOMapper.fromUserModelToUserDTO(ruser.get());
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User id:"+id+", not found",null);
    }

    @RequestMapping(method=RequestMethod.GET,value="/user/login/{login}")
    private UserDTO getUserByLogin(@PathVariable String login) {
        Optional<UserModel> ruser;
        ruser = userService.getUserNoPwdByLogin(login);
        if(ruser.isPresent()) {
            return DTOMapper.fromUserModelToUserDTO(ruser.get());
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User login"+login+", not found",null);
    }

//    @RequestMapping(method=RequestMethod.POST,value="/user")
//    @Transactional
//    public LoginResponse addUser(@RequestBody UserDTO user) {
//        System.out.println(user);
//        // TODO : Faire la connexion en meme temps
//        UserDTO userSaved = userService.addUser(user);
//
//        final UserDetails userDetails = authService.loadUserByUsername(userSaved.getLogin());
//        final String token = jwtTokenUtil.generateToken(userDetails);
//
//
//        return (new LoginResponse(token, userSaved));
//    }

    @RequestMapping(method=RequestMethod.POST,value="/user")
    @Transactional // TODO : PROBLEMS
    public LoginResponse addUser(@RequestBody UserDTO user) {
        System.out.println(user);
        // TODO : Faire la connexion en meme temps
        UserDTO userSaved = userService.addUser(user);

        final UserDetails userDetails = authService.loadUserByUsername(userSaved.getLogin());
        final String token = jwtTokenUtil.generateToken(userDetails);


        return (new LoginResponse(token, userSaved));
    }

    @RequestMapping(method=RequestMethod.PUT,value="/user/{id}")
    public UserDTO updateUser(@RequestBody UserDTO user,@PathVariable String id) {
        user.setId(Integer.valueOf(id));
        return userService.updateUser(user);
    }

    @RequestMapping(method=RequestMethod.PUT,value="/user/{id}/role")
    public UserDTO addRoleToUser(@RequestBody RoleDTO roles, @PathVariable String id) {
        return userService.addRoleToUser(roles, Integer.valueOf(id));
    }

    @RequestMapping(method=RequestMethod.DELETE,value="/user/{id}")
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser(Integer.valueOf(id));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/user/roles")
    public ResponseEntity<Map<String, List<String>>> addRole(@RequestBody RoleDTO roles) {
        Map<String, List<String>> result = userService.addRoles(roles);

        return ResponseEntity.ok(result);
    }

}
