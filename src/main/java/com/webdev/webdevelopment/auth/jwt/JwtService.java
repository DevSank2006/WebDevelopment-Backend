package com.webdev.webdevelopment.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;


@Service
public class JwtService {
    String secretKey="Q7mN2xV9pL4kR8sT1wZ6cH3jF5aB0uY8nD2eG9qK7vM4xP1sR6tW3zL8cN5hJ0";
    public String generateToken(String email){


        return Jwts.builder()
                .subject(email)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(20, ChronoUnit.HOURS)))
                .compact();
    }


    public String getSubject(String token){
        Claims claims=extractAllClaims(token);
        return claims.getSubject();
    }
    public Boolean isExpired(String token){
        Claims claims=extractAllClaims(token);
        if(claims.getExpiration().before(new Date())){
            return true;
        }else{
            return false;
        }
    }



    public Claims extractAllClaims(String token){
        SecretKey key=Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();


    }
}
