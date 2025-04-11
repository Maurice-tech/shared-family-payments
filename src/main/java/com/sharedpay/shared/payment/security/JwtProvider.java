package com.sharedpay.shared.payment.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;



@Component
public class JwtProvider {

    private SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());


    public String generateToken(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roles = populateAuthorities(authorities);
        System.out.println("Roles being set in JWT: " + roles);


        Date now = new Date();

        String jwt = Jwts.builder().setIssuedAt(new Date())
                .setSubject(authentication.getName())
                .setIssuedAt(now)
                .setExpiration(new Date(new Date().getTime() + JwtConstant.ACCESS_TOKEN_EXPIRATION_TIME))
                .claim("authorities", roles)
                .signWith(key)
                .compact();

        return jwt;
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> auths = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if (role.startsWith("ROLE_")) {
                auths.add(role);
            } else {
                auths.add("ROLE_" + role);
            }
        }
        return String.join(",", auths);
    }
}
