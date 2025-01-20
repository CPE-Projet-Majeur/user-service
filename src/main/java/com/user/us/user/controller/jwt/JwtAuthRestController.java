package com.user.us.user.controller.jwt;

import com.user.us.user.common.tools.JwtTokenUtil;
import com.user.us.user.model.JwtRequest;
import com.user.us.user.model.JwtResponse;
import com.user.us.user.model.LoginResponse;
import com.user.us.user.model.UserDTO;
import com.user.us.user.service.AuthService;
import com.user.us.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class JwtAuthRestController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    AuthService authService;
    @Autowired
    UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
//        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
//        final UserDetails userDetails = authService.loadUserByUsername(authenticationRequest.getUsername());
//        final String token = jwtTokenUtil.generateToken(userDetails);
//        UserDTO user = new UserDTO(userService.getUserByLogin(userDetails.getUsername())
//                .orElseThrow(() -> new RuntimeException("User not found in database")));
//        // réponse avec le token et l'utilisateur
//        return ResponseEntity.ok(new LoginResponse(token, user));
        authenticate(authenticationRequest.getLogin(), authenticationRequest.getPassword());
        final UserDetails userDetails = authService.loadUserByUsername(authenticationRequest.getLogin());
        final String token = jwtTokenUtil.generateToken(userDetails);
        // It does the login/username match alone 
        UserDTO user = new UserDTO(userService.getUserByLogin(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found in database")));
        // réponse avec le token et l'utilisateur
        return ResponseEntity.ok(new LoginResponse(token, user));
    }


    private void authenticate(String username, String password) throws Exception {
        try {
            System.out.println(username + password);
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }

    }
}
