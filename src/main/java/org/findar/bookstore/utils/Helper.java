package org.findar.bookstore.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.findar.bookstore.configuartions.JwtService;
import org.findar.bookstore.dtos.GetPaginatedDto;
import org.findar.bookstore.dtos.ViewBookDto;
import org.findar.bookstore.entities.BlackListEntity;
import org.findar.bookstore.entities.Book;
import org.findar.bookstore.entities.User;
import org.findar.bookstore.exception.NotFound;
import org.findar.bookstore.exception.UnathuorizedAccess;
import org.findar.bookstore.repositories.BlackListRepository;
import org.findar.bookstore.repositories.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Helper {

    private final JwtService jwtService;
    private final BlackListRepository blackListRepo;
    private final UserRepository userRepo;

    public User extractUser(HttpServletRequest request, HttpServletResponse response){
        String jwt = getJwt(request);
        this.checkBlackListedSession(request,response);
        String userId= jwtService.extractUsername(jwt);
        return userRepo.findById(userId).orElseThrow(()->new NotFound("Customer not found"));
    }
    public void checkBlackListedSession(HttpServletRequest request, HttpServletResponse response){
        String jwt = getJwt(request);
        Optional<BlackListEntity> blackListed = blackListRepo.findByToken(jwt);
        if(blackListed.isPresent()){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new UnathuorizedAccess("Token is expired");
        }
    }
    public void validateSession(HttpServletRequest request,HttpServletResponse response){
        this.checkBlackListedSession(request, response);
        String jwt = getJwt(request);
        User user = extractUser(request,response);
        if(!jwtService.isTokenValid(jwt, user)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new UnathuorizedAccess("Token is expired");
        }
    }
    public String getJwt(HttpServletRequest request){
        final String authHeader = request.getHeader("Authorization");
        return authHeader.substring(7);
    }
    public String hash(String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }
    public boolean verifyPassword(String password, String hash){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(password,hash);
    }
    private Page<ViewBookDto> getPageable(Pageable pageable, int start, List<ViewBookDto> requests) {
        start = pageable.getPageNumber() * pageable.getPageSize();
        int end = Math.min(start + pageable.getPageSize(), requests.size());
        List<ViewBookDto> sublist = requests.subList(start, end);
        return new PageImpl<>(sublist, pageable, sublist.size());

    }
    public Page<ViewBookDto> getBookPagedResponse(GetPaginatedDto getPaginatedDto, List<Book> all){
        Pageable pageable =  PageRequest.of(getPaginatedDto.getPage(),getPaginatedDto.getSize());
        List<ViewBookDto> bookDtos = new ArrayList<>();
        for(Book each: all){
            ViewBookDto viewBookDto = new ViewBookDto();
            BeanUtils.copyProperties(each,viewBookDto);
            bookDtos.add(viewBookDto);
        }
        return this.getPageable(pageable, getPaginatedDto.getStart(), bookDtos);
    }



}
