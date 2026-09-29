package org.example.conta.web.dto;

public record LoginResponse(String token, String tipo) {
    public LoginResponse(String token) {
        this(token, "Bearer");
    }
}
