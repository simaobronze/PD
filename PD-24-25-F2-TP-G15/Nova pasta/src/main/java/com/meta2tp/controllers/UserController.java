package com.meta2tp.controllers;

import com.meta2tp.models.User;
import com.meta2tp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Endpoint para registrar um usuário
    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.register(user);
    }

    // Endpoint para autenticar um usuário
    @PostMapping("/authenticate")
    public String authenticateUser(@RequestBody User user) {
        return userService.authenticate(user);
    }
}
