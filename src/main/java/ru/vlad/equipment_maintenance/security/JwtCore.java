package ru.vlad.equipment_maintenance.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtCore {

    // Если переменная не найдена в application.properties, подставится дефолтный безопасный ключ через двоеточие ":"
    @Value("${vlad.app.secret:9a4f2c8d3e1b7f6a5c4d3e2b1a0f9e8d7c6b5a4f3e2d1c0b9a8f7e6d5c4b3a2f}")
    private String secret;

    // Если переменная не найдена, подставится дефолтное время жизни (86400000 мс = 24 часа)
    @Value("${vlad.app.lifetime:86400000}")
    private int lifetime;

    // В JJWT 0.12.x используется строго типизированный SecretKey вместо обычной строки
    private SecretKey getSigningKey() {
        // Проверяем, если ключ в формате HEX (как наш дефолтный), то кодируем его в байты напрямую.
        // Если это Base64 строка из пропертис, используем Decoders.BASE64
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (Exception e) {
            keyBytes = secret.getBytes();
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + lifetime))
                .signWith(getSigningKey()) // Новый синтаксис принимает сгенерированный SecretKey
                .compact();
    }

    public String getNameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Вместо parserBuilder() используем parser().verifyWith()
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // Токен невалиден, истек или изменен
            return false;
        }
    }
}