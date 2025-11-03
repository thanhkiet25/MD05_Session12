package com.ra.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JWTProvider {
  @Value("${jwt.secret}")
  private  String secret;
  @Value("${jwt.expiration}")
  private  long expiration;
   // tạo khoa ký để tăng bảo mặtvà tránh quá ngắn
private Key getSigningKey(){
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
}
    // Sinh token
    public String generateToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // Kiểm tra token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
            // nếu hết hạn
        } catch (ExpiredJwtException e) {
            System.err.println("❌ Token expired: " + e.getMessage());
            //nếu lỗi mã khóa hay ....
        } catch (JwtException e) {
            System.err.println("❌ Invalid token: " + e.getMessage());
        }
        return false;
    }
    // Lấy username từ token
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
