package org.findar.bookstore.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.findar.bookstore.dtos.GlobalResponse;
import org.findar.bookstore.dtos.LoginDto;
import org.findar.bookstore.dtos.RegisterDto;
import org.findar.bookstore.services.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("register")
    public GlobalResponse<?> registerUser(@RequestBody RegisterDto dto, HttpServletResponse httpServletResponse){
        return userService.registerUser(dto, httpServletResponse);
    }

    @PostMapping("logout")
    public GlobalResponse<?> logoutUser(HttpServletRequest httpServletRequest, @RequestHeader("Authorization") String token, HttpServletResponse httpServletResponse){
        return userService.logoutUser(httpServletRequest,httpServletResponse);
    }

    @PostMapping("login")
    public GlobalResponse<String> loginUser(@RequestBody LoginDto loginDto, HttpServletResponse httpServletResponse){
        return userService.loginUser(loginDto, httpServletResponse);
    }
}
