package com.EMS.security.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey = "rcgg106a1serv1ces";

    @Value("${security.jwt.token.expire-length:14400000}")
    private long validityInMilliseconds = 14400000;//14400000 4h , 3600000; // 1h

    @Autowired
    private UserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String username, String role,long roleId) {
    	
    	Claims claims = Jwts.claims().setSubject(CryptoUtil.encrypt(username));
        claims.put("roles", CryptoUtil.encrypt(role));
        claims.put("roleId", CryptoUtil.encrypt(new Long(roleId).toString()));
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()//
            .setClaims(claims)//
            .setIssuedAt(now)//
            .setExpiration(validity)//
            .signWith(SignatureAlgorithm.HS256, secretKey)//
            .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        //return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    	String userName=null;
    	try {
			userName =CryptoUtil.decryptE(Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject());
		} catch (Exception e) {
			e.printStackTrace();
		}
        return userName;
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);

            if (claims.getBody().getExpiration().before(new Date())) {
                return false;
            }

            return true;
        } catch (JwtException | IllegalArgumentException e) {
        	throw new InvalidJwtAuthenticationException("Expired or invalid JWT token");
        }
    }
    
    public Long getRoleId(String token)  {
       // return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("roleId", Long.class);
    	String roleId=null;
    	try {
			roleId= CryptoUtil.decryptE(Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("roleId", String.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return new Long(roleId).longValue();
    	
    }

}
