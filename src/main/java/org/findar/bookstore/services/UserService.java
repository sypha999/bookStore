package org.findar.bookstore.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.findar.bookstore.configuartions.JwtService;
import org.findar.bookstore.dtos.GlobalResponse;
import org.findar.bookstore.dtos.LoginDto;
import org.findar.bookstore.dtos.RegisterDto;
import org.findar.bookstore.entities.BlackListEntity;
import org.findar.bookstore.entities.User;
import org.findar.bookstore.exception.BadRequest;
import org.findar.bookstore.repositories.BlackListRepository;
import org.findar.bookstore.repositories.UserRepository;
import org.findar.bookstore.utils.Helper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final Helper helper;
    private final BlackListRepository blackListRepository;
    private final JwtService jwtService;

    public GlobalResponse<?> registerUser(RegisterDto dto, HttpServletResponse httpServletResponse) {
        try{
            Optional<User> alreadyRegisteredUser = userRepository.findByEmail(dto.getEmail());
            if (alreadyRegisteredUser.isPresent()) {
                throw new BadRequest("Customer with email "+dto.getEmail()+" already exists");
            }
            if(!dto.getPassword().equals(dto.getConfirmPassword())) {
                throw new BadRequest("Passwords do not match");
            }
            User user = new User();
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setEmail(dto.getEmail());
            user.setPassword(helper.hash(dto.getPassword()));
            user.setPhoneNumber(dto.getPhoneNumber());
            userRepository.saveAndFlush(user);

            GlobalResponse<String> response = new GlobalResponse<>();
            response.setStatus(HttpStatus.ACCEPTED);
            response.setMessage("Customer registered successfully");
            httpServletResponse.setStatus(200);
            return response;
        }
        catch (Exception e){
            GlobalResponse<String> response = new GlobalResponse<>();
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setMessage("Customer could not be registered");
            response.setDebugMessage(e.getMessage());
            httpServletResponse.setStatus(400);
            return response;
        }
    }
    public GlobalResponse<?> logoutUser(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            helper.validateSession(httpServletRequest,httpServletResponse);
            helper.checkBlackListedSession(httpServletRequest, httpServletResponse);
            String token = httpServletRequest.getHeader("Authorization").substring(7);
            BlackListEntity blackList = new BlackListEntity();
            blackList.setToken(token);
            blackListRepository.saveAndFlush(blackList);
            GlobalResponse<String> response = new GlobalResponse<>();
            response.setStatus(HttpStatus.ACCEPTED);
            response.setMessage("Logout successful");
            httpServletResponse.setStatus(200);
            return response;
        }
        catch (Exception e){
            GlobalResponse<String> response = new GlobalResponse<>();
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setMessage("Could not logout user");
            response.setDebugMessage(e.getMessage());
            httpServletResponse.setStatus(400);
            return response;
        }
    }
    public GlobalResponse<String> loginUser(LoginDto loginDto, HttpServletResponse httpServletResponse) {
        try {
            User alreadyRegisteredUser = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(()->new BadRequest("Invalid username or password"));
            if(!helper.verifyPassword(loginDto.getPassword(), alreadyRegisteredUser.getPassword())){
                throw new BadRequest("Invalid username or password");
            }
            String token = jwtService.generateToken(alreadyRegisteredUser);
            GlobalResponse<String> response = new GlobalResponse<>();
            response.setData(token);
            response.setStatus(HttpStatus.ACCEPTED);
            response.setMessage("login successful");
            httpServletResponse.setStatus(200);
            return response;
        }
        catch (Exception e){
            GlobalResponse<String> response = new GlobalResponse<>();
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setMessage("Could not login user");
            response.setDebugMessage(e.getMessage());
            httpServletResponse.setStatus(400);
            return response;
        }
    }

}
