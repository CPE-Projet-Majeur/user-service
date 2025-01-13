package com.user.us.user.controller;

import com.user.us.user.service.AuthService;

import com.user.us.user.model.AuthDTO;
import com.user.us.user.model.UserModel;
//import com.user.us.user.service.JWTService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// Todo : not used anymore i think

@CrossOrigin
@RestController
public class AuthRestController {
    private final AuthService authService;
//    private JWTService jwtService;

    public AuthRestController(AuthService userService){//, JWTService jwtService) {
        this.authService = userService;
//        this.jwtService = jwtService;

    }

    @RequestMapping(method= RequestMethod.POST,value="/auth")
    private Integer login(@RequestBody AuthDTO authDto) {
        List<UserModel> uList = authService.getUserByLoginPwd(authDto.getUsername(),authDto.getPassword());
//        String token = jwtService.generateToken(authentication);
        if( uList.size() > 0) {
            return uList.get(0).getId();
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Authentification Failed",null);

    }

}
