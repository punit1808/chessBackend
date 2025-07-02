package com.chessmaster.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.chessmaster.Models.Users;
import com.chessmaster.Service.UserService;

import java.util.List;

@RequestMapping("/api/v1/auth/users")
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Users>> allUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List <Users> users = userService.allUsers();
        return ResponseEntity.ok(users);
    }
}