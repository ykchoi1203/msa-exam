package com.sparta.msa_exam.auth.dto;

public record SignInRequest(
        String username,
        String password
) {
}
