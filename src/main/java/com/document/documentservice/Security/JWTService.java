package com.document.documentservice.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;

@Service
public class JWTService {

    @Value("${jwtSignKey}")
    private String jwrKey;
    private final static Logger logger = LoggerFactory.getLogger(JWTService.class);


    public Claims parseToken(String token) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(jwrKey);
            Key key = Keys.hmacShaKeyFor(decodedKey);

            logger.info("Validating token: {}", token);
            logger.info("Using signing key: {}", jwrKey);

            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired", e);
        } catch (SignatureException e) {
            throw new RuntimeException("Invalid token signature", e);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token", e);
        }

    }
}
