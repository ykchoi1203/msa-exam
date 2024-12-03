package com.sparta.msa_exam.auth.service;

import com.sparta.msa_exam.auth.dto.SignInRequest;
import com.sparta.msa_exam.auth.dto.SignUpRequest;
import com.sparta.msa_exam.auth.entity.User;
import com.sparta.msa_exam.auth.entity.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${service.jwt.access-expiration}")
    private Long accessExpiration;

    private final SecretKey secretKey;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthService(@Value("${service.jwt.secret-key}") String secretKey, UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String createAccessToken(SignInRequest request) {
        User user = userRepository.findByUsername(request.username()).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if(!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Wrong password");
        }

        return Jwts.builder()
                .claim("username", user.getUsername())
                .claim("user_id", user.getUserId())
                .issuer(issuer)
                .issuedAt(new Date((System.currentTimeMillis())))
                .expiration(new Date((System.currentTimeMillis() + accessExpiration)))
                .signWith(secretKey)
                .compact();
    }

    public String signUp(SignUpRequest request) {
        if(userRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("Username is already in use");
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .build();

        user = userRepository.save(user);

        return user.getUsername();
    }


    public String getUsername(String authentication) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(authentication)
                .getPayload();

        // JWT의 "username" 클레임에서 값 추출
        return claims.get("username", String.class);
    }
}