package com.capestone.hrms_backend.util;

import com.capestone.hrms_backend.security.HrmsUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt-secret-key}")
    private String secret;

    @Value("${jwt-expiration}")
    private long expiration;

    private Key getSignedKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(HrmsUserDetails user){
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("id", user.getEmployeeId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignedKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parseClaims(String token){
        return Jwts.parserBuilder()
                   .setSigningKey(getSignedKey())
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    public String getEmail(String token){
        return parseClaims(token).getSubject();
    }

    public Date getExpiration(String token){
        return parseClaims(token).getExpiration();
    }

    public boolean isExpired(String token){
        Date expiry = getExpiration(token);
        return expiry.before(new Date());
    }

    public boolean isValid(String token){
        try{parseClaims(token); return true;}
        catch(Exception e){return false;}
    }



}