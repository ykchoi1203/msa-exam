package com.sparta.msa_exam.auth.controller;


import com.sparta.msa_exam.auth.dto.AuthResponse;
import com.sparta.msa_exam.auth.dto.SignInRequest;
import com.sparta.msa_exam.auth.dto.SignUpRequest;
import com.sparta.msa_exam.auth.dto.SignUpResponse;
import com.sparta.msa_exam.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    @Value("${server.port}")
    private String port;

    private final AuthService authService;

    private final HttpHeaders headers = new HttpHeaders();


    @PostMapping("/auth/signIn")
    public ResponseEntity<?> createAuthToken(@RequestBody SignInRequest request) {
        headers.set("server-port", port);
        return ResponseEntity.ok().headers(headers).body(new AuthResponse(authService.createAccessToken(request)));
    }

    @PostMapping("/auth/signUp")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest request) {
        headers.set("server-port", port);
        return ResponseEntity.ok().headers(headers).body(new SignUpResponse(authService.signUp(request)));
    }

    @GetMapping("/auth/username")
    public ResponseEntity<?> getUsername(@RequestHeader("Authorization") String authentication) {
        headers.set("server-port", port);
        return ResponseEntity.ok().headers(headers).body(authService.getUsername(authentication.substring(7)));
    }



}
