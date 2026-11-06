package com.webdev.webdevelopment.auth.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader=request.getHeader("Authorization");
        String token="";

        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }else{
            token=authHeader.substring(7);
        }

        System.out.println("AUTH HEADER = [" + authHeader + "]");
        System.out.println("TOKEN = [" + token + "]");
        System.out.println("DOT COUNT = " +
                token.chars().filter(c -> c == '.').count());

        System.out.println("REQUEST = " + request.getMethod() + " " + request.getRequestURI());
        System.out.println("AUTH HEADER = " + authHeader);
        if(token!=null && SecurityContextHolder.getContext().getAuthentication()==null ){
            Claims claims=jwtService.extractAllClaims(token);

            System.out.println("SUBJECT = " + claims.getSubject());
            System.out.println("EXPIRED = " + jwtService.isExpired(token));

            if(!jwtService.isExpired(token)){
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                claims.getSubject(),
                                null,
                                new ArrayList<>()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("AUTHENTICATION SET = " +
                        SecurityContextHolder.getContext().getAuthentication());
            }
        }
        filterChain.doFilter(request,response);



    }
}
