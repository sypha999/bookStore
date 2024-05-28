package org.findar.bookstore.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.findar.bookstore.configuartions.JwtService;
import org.findar.bookstore.dtos.GlobalResponse;
import org.findar.bookstore.dtos.LoginDto;
import org.findar.bookstore.dtos.RegisterDto;
import org.findar.bookstore.entities.User;
import org.findar.bookstore.repositories.UserRepository;
import org.findar.bookstore.utils.Helper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {UserService.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
@RequiredArgsConstructor
class UserServiceTest {

    @MockBean
    private  Helper helper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @Test
    void testRegisterUser() {
        // Arrange
        User user = new User();
        user.setCreatedAt(LocalDate.of(2024, 5, 27).atStartOfDay());
        user.setEmail("datonarinze@gmail.com");
        user.setFirstName("Arinze");
        user.setIsDeleted(false);
        user.setIsEnabled(true);
        user.setLastName("Anthony");
        user.setPassword("12345");
        user.setPhoneNumber("07067847829");
        user.setUpdatedAt(LocalDate.of(2024, 5, 28).atStartOfDay());
        user.setUserId(UUID.randomUUID().toString());
        when(userRepository.saveAndFlush(Mockito.<User>any())).thenReturn(user);
        Optional<User> emptyResult = Optional.empty();
        when(userRepository.findByEmail(Mockito.<String>any())).thenReturn(emptyResult);
        when(helper.hash(Mockito.<String>any())).thenReturn("Hash");

        RegisterDto dto = new RegisterDto();
        dto.setConfirmPassword("12345");
        dto.setEmail("datonarinze@gmail.com");
        dto.setFirstName("Arinze");
        dto.setLastName("Anthony");
        dto.setPassword("12345");
        dto.setPhoneNumber("07067847829");
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

        // Act
        GlobalResponse<?> actualRegisterUserResult = userService.registerUser(dto, httpServletResponse);

        // Assert
        verify(userRepository).findByEmail(eq("datonarinze@gmail.com"));
        verify(helper).hash(eq("12345"));
        verify(userRepository).saveAndFlush(isA(User.class));
        assertEquals("Customer registered successfully", actualRegisterUserResult.getMessage());
        assertEquals(200, httpServletResponse.getStatus());
        assertEquals(HttpStatus.ACCEPTED, actualRegisterUserResult.getStatus());
    }

    @Test
    void testLogoutUser() {
        // Arrange
        doNothing().when(helper)
                .checkBlackListedSession(Mockito.<HttpServletRequest>any(), Mockito.<HttpServletResponse>any());
        doNothing().when(helper).validateSession(Mockito.<HttpServletRequest>any(), Mockito.<HttpServletResponse>any());
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

        // Act
        GlobalResponse<?> actualLogoutUserResult = userService.logoutUser(httpServletRequest, httpServletResponse);

        // Assert
        verify(helper).checkBlackListedSession(isA(HttpServletRequest.class), isA(HttpServletResponse.class));
        verify(helper).validateSession(isA(HttpServletRequest.class), isA(HttpServletResponse.class));
        assertEquals(
                "Cannot invoke \"String.substring(int)\" because the return value of \"jakarta.servlet.http.HttpServletRequest"
                        + ".getHeader(String)\" is null",
                actualLogoutUserResult.getDebugMessage());
        assertEquals("Could not logout user", actualLogoutUserResult.getMessage());
        assertEquals(400, httpServletResponse.getStatus());
        assertEquals(HttpStatus.BAD_REQUEST, actualLogoutUserResult.getStatus());
    }

    @Test
    void testLoginUser() {
        // Arrange
        User user = new User();
        user.setCreatedAt(LocalDate.of(2024, 5, 27).atStartOfDay());
        user.setEmail("datonarinze@gmail.com");
        user.setFirstName("Arinze");
        user.setIsDeleted(false);
        user.setIsEnabled(true);
        user.setLastName("Anthony");
        user.setPassword("12345");
        user.setPhoneNumber("07067847829");
        user.setUpdatedAt(LocalDate.of(2024, 5, 28).atStartOfDay());
        user.setUserId(UUID.randomUUID().toString());
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findByEmail(Mockito.<String>any())).thenReturn(ofResult);
        when(helper.verifyPassword(Mockito.<String>any(), Mockito.<String>any())).thenReturn(true);
        when(jwtService.generateToken(Mockito.<UserDetails>any())).thenReturn("ABC123");

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("datonarinze@gmail.com");
        loginDto.setPassword("12345");
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

        // Act
        GlobalResponse<String> actualLoginUserResult = userService.loginUser(loginDto, httpServletResponse);

        // Assert
        verify(jwtService).generateToken(isA(UserDetails.class));
        verify(userRepository).findByEmail(eq("datonarinze@gmail.com"));
        verify(helper).verifyPassword(eq("12345"), eq("12345"));
        assertEquals("login successful", actualLoginUserResult.getMessage());
        assertEquals(200, httpServletResponse.getStatus());
        assertEquals(HttpStatus.ACCEPTED, actualLoginUserResult.getStatus());
    }


}
