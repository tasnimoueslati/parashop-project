package com.parapharmacie.dto;

public record AuthResponse(String token, String email, String role) {
}
