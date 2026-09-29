package org.example.transferencia.web;

import java.util.List;
import org.example.transferencia.application.ConsultarTransferenciaUseCase;
import org.example.transferencia.application.IniciarTransferenciaUseCase;
import org.example.transferencia.domain.Transferencia;
import org.example.transferencia.web.dto.IniciarTransferenciaCommand;
import org.example.transferencia.web.dto.TransferenciaResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transferencias")
public class TransferenciaController {

    private final IniciarTransferenciaUseCase iniciarTransferenciaUseCase;
    private final ConsultarTransferenciaUseCase consultarTransferenciaUseCase;

    public TransferenciaController(
            IniciarTransferenciaUseCase iniciarTransferenciaUseCase,
            ConsultarTransferenciaUseCase consultarTransferenciaUseCase
    ) {
        this.iniciarTransferenciaUseCase = iniciarTransferenciaUseCase;
        this.consultarTransferenciaUseCase = consultarTransferenciaUseCase;
    }

    @PostMapping
    public ResponseEntity<TransferenciaResponse> iniciar(@RequestBody IniciarTransferenciaCommand command) {
        Long id = iniciarTransferenciaUseCase.execute(command);
        Transferencia t = consultarTransferenciaUseCase.porId(id)
                .orElseThrow(() -> new IllegalStateException("Transferencia nao encontrada apos criacao"));
        return ResponseEntity.status(HttpStatus.CREATED).body(TransferenciaResponse.from(t));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransferenciaResponse> buscarPorId(@PathVariable("id") Long id) {
        return consultarTransferenciaUseCase.porId(id)
                .map(t -> ResponseEntity.ok(TransferenciaResponse.from(t)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TransferenciaResponse>> listarTodas() {
        List<TransferenciaResponse> lista = consultarTransferenciaUseCase.todas()
                .stream()
                .map(TransferenciaResponse::from)
                .toList();
        return ResponseEntity.ok(lista);
    }
}