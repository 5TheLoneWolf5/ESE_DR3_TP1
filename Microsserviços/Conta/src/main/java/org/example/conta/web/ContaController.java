package org.example.conta.web;

import java.math.BigDecimal;
import java.util.List;
import org.example.conta.application.ConsultarContaUseCase;
import org.example.conta.application.CreditarContaUseCase;
import org.example.conta.application.CriarContaUseCase;
import org.example.conta.application.DebitarContaUseCase;
import org.example.conta.domain.Conta;
import org.example.conta.domain.ContaRepository;
import org.example.conta.domain.SaldoInsuficienteException;
import org.example.conta.web.dto.ContaResponse;
import org.example.conta.web.dto.CriarContaRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contas-banco")
public class ContaController {

    private final ConsultarContaUseCase consultarContaUseCase;
    private final CriarContaUseCase criarContaUseCase;
    private final DebitarContaUseCase debitarContaUseCase;
    private final CreditarContaUseCase creditarContaUseCase;
    private final ContaRepository contaRepository;

    public ContaController(
            ConsultarContaUseCase consultarContaUseCase,
            CriarContaUseCase criarContaUseCase,
            DebitarContaUseCase debitarContaUseCase,
            CreditarContaUseCase creditarContaUseCase,
            ContaRepository contaRepository
    ) {
        this.consultarContaUseCase = consultarContaUseCase;
        this.criarContaUseCase = criarContaUseCase;
        this.debitarContaUseCase = debitarContaUseCase;
        this.creditarContaUseCase = creditarContaUseCase;
        this.contaRepository = contaRepository;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ContaResponse>> getContas() {
        List<ContaResponse> contas = consultarContaUseCase.todas()
                .stream()
                .map(ContaResponse::from)
                .toList();
        return ResponseEntity.ok(contas);
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<ContaResponse> getConta(@PathVariable("id") Long id) {
        return consultarContaUseCase.porId(id)
                .map(c -> ResponseEntity.ok(ContaResponse.from(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/adicionar")
    public ResponseEntity<String> addConta(@RequestBody CriarContaRequest request) {
        Long id = criarContaUseCase.execute(request.nome(), request.saldoInicial(), request.moeda());
        return ResponseEntity.status(HttpStatus.CREATED).body("Conta criada com sucesso! ID: " + id);
    }

    @PutMapping("/debitar/{id}/{saldo}")
    public ResponseEntity<String> debitarSaldo(
            @PathVariable("id") Long id,
            @PathVariable("saldo") BigDecimal saldo,
            @RequestHeader(value = "X-Idempotency-Key", required = false) String chaveIdempotencia,
            @RequestHeader(value = "X-Correlation-Id", required = false) Long correlationId
    ) {
        debitarContaUseCase.execute(id, saldo, chaveIdempotencia, correlationId);
        return ResponseEntity.ok("Saldo da conta debitado com sucesso!");
    }

    @PutMapping("/creditar/{id}/{saldo}")
    public ResponseEntity<String> creditarSaldo(
            @PathVariable("id") Long id,
            @PathVariable("saldo") BigDecimal saldo,
            @RequestHeader(value = "X-Idempotency-Key", required = false) String chaveIdempotencia,
            @RequestHeader(value = "X-Correlation-Id", required = false) Long correlationId
    ) {
        creditarContaUseCase.execute(id, saldo, chaveIdempotencia, correlationId);
        return ResponseEntity.ok("Saldo da conta creditado com sucesso!");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteConta(@PathVariable("id") Long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada."));
        contaRepository.delete(conta);
        return ResponseEntity.ok("Conta deletada com sucesso!");
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<String> handleSaldoInsuficiente(SaldoInsuficienteException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}