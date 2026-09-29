package org.example.conta.web;

import org.example.conta.application.LoginUseCase;
import org.example.conta.application.RegistrarClienteUseCase;
import org.example.conta.web.dto.LoginRequest;
import org.example.conta.web.dto.LoginResponse;
import org.example.conta.web.dto.RegistrarClienteRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegistrarClienteUseCase registrarClienteUseCase;

    public AuthController(LoginUseCase loginUseCase, RegistrarClienteUseCase registrarClienteUseCase) {
        this.loginUseCase = loginUseCase;
        this.registrarClienteUseCase = registrarClienteUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = loginUseCase.execute(request.nome(), request.senha());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/clientes/registrar")
    public ResponseEntity<String> registrar(@RequestBody RegistrarClienteRequest request) {
        Long clienteId = registrarClienteUseCase.execute(request.nome(), request.senha());
        return ResponseEntity.status(HttpStatus.CREATED).body("Cliente registrado com sucesso! ID: " + clienteId);
    }
}
