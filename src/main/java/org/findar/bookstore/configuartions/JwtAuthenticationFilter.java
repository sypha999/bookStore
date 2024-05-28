package org.findar.bookstore.configuartions;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.findar.bookstore.dtos.GlobalResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.PrintWriter;



@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userId;
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            jwt = authHeader.substring(7);
            userId = jwtService.extractUsername(jwt);
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

                var isTokenValid = jwtService.isTokenValid(jwt, userDetails);
                if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            this.writeErrorResponse("Invalid token", response, e.getMessage());
        }
    }


    private void writeErrorResponse(String errMsg, HttpServletResponse response,String debugMessage) {
        try {
            GlobalResponse ar = new GlobalResponse();
            ar.setMessage(errMsg);
            response.setStatus(403);
            ar.setDebugMessage(debugMessage);
            ar.setStatus(HttpStatus.FORBIDDEN);
            response.setContentType("application/json");
            ObjectMapper mapper = new ObjectMapper();
            PrintWriter out = response.getWriter();
            out.write(mapper.writeValueAsString(ar));
        }catch (Exception e){
            logger.error("Unknown error", e);
        }
    }
}