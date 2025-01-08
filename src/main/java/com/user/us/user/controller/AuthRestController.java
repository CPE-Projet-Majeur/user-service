package com.user.us.user.controller;

import com.user.us.user.service.AuthService;

import com.user.us.user.model.AuthDTO;
import com.user.us.user.model.UserModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin
@RestController
public class AuthRestController {
    private final AuthService authService;

    public AuthRestController(AuthService userService) {
        this.authService=userService;
    }

    @RequestMapping(method= RequestMethod.POST,value="/auth")
    private Integer login(@RequestBody AuthDTO authDto) {
        List<UserModel> uList = authService.getUserByLoginPwd(authDto.getUsername(),authDto.getPassword());
        if( uList.size() > 0) {
            return uList.get(0).getId();
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Authentification Failed",null);

    }

}
