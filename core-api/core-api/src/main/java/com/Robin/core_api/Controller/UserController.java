package com.Robin.core_api.Controller;

import com.Robin.core_api.Model.Entity.User;
import com.Robin.core_api.Service.JwtService;
import com.Robin.core_api.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user){
        User registered = userService.register(user);
        return new ResponseEntity<>(registered, HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<?>login(@RequestBody User user){
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

            if(authentication.isAuthenticated()){
                //if verified , generate and return the token
                String token = jwtService.generateToken(user.getEmail());
                return new ResponseEntity<>(token,HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }catch (AuthenticationException e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
