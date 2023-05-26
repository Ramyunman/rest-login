package com.lcomputerstudy.example.config;

import java.util.Date;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import com.lcomputerstudy.example.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class JwtUtils {

	private static final String jwtSecret = "lcomputerstudyexample"; //원하는 시크릿키로 수정
	
	private static final int jwtExpirationMs = 864000;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//jwt 생성 메소드
	public String generateJwtToken(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		
		// JWT 토큰 생성
		String jwtToken = Jwts.builder()
				.setSubject(user.getUsername())
				.setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encodeToString(jwtSecret.getBytes(StandardCharsets.UTF_8)))
				.compact();
		
		return jwtToken;
	}
	
	//토큰을 이용하여 유저 아이디 불러오는 메소드
	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}
	
	 private static Claims getClaimsFormToken(String token) {
		 //byte[] jwtSecretBytes1 = Base64.getDecoder().decode(jwtSecret);
		 byte[] jwtSecretBytes2 = DatatypeConverter.parseBase64Binary(jwtSecret);
		 //System.out.println(jwtSecretBytes1);
		 System.out.println(jwtSecretBytes2);
         return (Claims) Jwts.parser().setSigningKey(jwtSecretBytes2).parseClaimsJws(token).getBody();
    }
	 
	 public static String getUserEmailFromToken(String token) {
	        Claims claims = getClaimsFormToken(token);
	        Map<String, Object> map = new HashMap<>(claims);
	        String username = (String) map.get("sub");
	        
	        return username;
	    }
	
	//jwt 유효성 검사 메소드
	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}
	
	
}
