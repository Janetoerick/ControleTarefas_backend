package com.project.tarefas.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.project.tarefas.model.User;

import javax.crypto.SecretKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final String secretKey;
    private final long jwtExpiration;
	
	public JwtService(
		    @Value("${application.security.jwt.secret-key}") String secretKey,
		    @Value("${application.security.jwt.expiration}") long jwtExpiration) {
		    
		    this.secretKey = secretKey;
		    this.jwtExpiration = jwtExpiration;
		}
	
	
	public String generateToken(User user) {
	    return generateToken((UserDetails) user); 
	}

	public String generateToken(UserDetails userDetails) {
	    return Jwts.builder()
	        .claims(new HashMap<>())
	        .subject(userDetails.getUsername())
	        .issuedAt(new Date(System.currentTimeMillis()))
	        .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
	        .signWith(getSignInKey())
	        .compact();
	}
	
	public String generateToken(Map<String, Object> extraClaims, User user) {
	    return Jwts
	        .builder()
	        .claims(extraClaims)
	        .subject(user.getUsername())
	        .issuedAt(new Date(System.currentTimeMillis()))
	        .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
	        .signWith(getSignInKey())
	        .compact();
	}
	
	public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
	
	public boolean isTokenValid(String token, UserDetails userDetails) {
	    try {
	        final String usernameExtraido = extractUsername(token);
	        
	        boolean isUsernameValid = usernameExtraido.equals(userDetails.getUsername());
	        boolean isTokenNotExpired = !isTokenExpired(token);
	        
	        return isUsernameValid && isTokenNotExpired;
	        
	    } catch (Exception e) {
	        return false;
	    }
	}

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
    	return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            if (expiration == null) {
                return true;
            }
            return expiration.before(new Date());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao verificar expiração do token: " + e.getMessage());
            return true;
        }
    }
    
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private SecretKey getSignInKey() { 
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes); 
    }
}
