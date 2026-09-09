package org.example.conta.web;

import org.example.conta.domain.Conta;
import org.example.conta.domain.ContaHistorico;
import org.example.conta.application.ContaService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/contas-banco")
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @GetMapping("/listar")
    public List<Conta> getContas() {
        return contaService.consultarContas();
    }

    @GetMapping("/listar/{id}")
    public Optional<Conta> getConta(@PathVariable("id") Long id) {
        return contaService.consultarConta(id);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteConta(@PathVariable("id") Long id) {
        contaService.excluirConta(id);
        return "Conta deletada com sucesso!";
    }

    @PostMapping("/adicionar")
    public String addConta(@RequestBody Conta conta) {
        contaService.incluirConta(conta);
        return "Conta criada com sucesso!";
    }

    @PutMapping("/debitar/{id}/{saldo}")
    public String debitarSaldo(@PathVariable("id") Long id, @PathVariable("saldo") BigDecimal saldo) {
        contaService.debitar(id, saldo);
        return "Saldo da conta alterada com sucesso!";
    }

    @PutMapping("/creditar/{id}/{saldo}")
    public String creditarSaldo(@PathVariable("id") Long id, @PathVariable("saldo") BigDecimal saldo) {
        contaService.creditar(id, saldo);
        return "Saldo da conta alterada com sucesso!";
    }

    @GetMapping("/historico")
    public List<ContaHistorico> getHistorico() {
        return contaService.consultarHistorico();
    }

    @GetMapping("/historico/{contaId}")
    public List<ContaHistorico> getHistoricoPorConta(@PathVariable("contaId") Long contaId) {
        return contaService.consultarHistoricoPorConta(contaId);
    }

}