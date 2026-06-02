package org.example.banco.controller;

import org.example.banco.entity.Conta;
import org.example.banco.service.ContaService;
import org.springframework.web.bind.annotation.*;

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
        return contaService.consultarContasDb();
    }

    @GetMapping("/listar/{id}")
    public Optional<Conta> getConta(@PathVariable("id") Long id) {
        return contaService.consultarContaDb(id);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteConta(@PathVariable("id") Long id) {
        contaService.excluirContaDb(id);
        return "Conta deletada com sucesso!";
    }

    @PostMapping("/adicionar")
    public String addConta(@RequestBody Conta conta) {
        contaService.incluirContaDb(conta);
        return "Conta criada com sucesso!";
    }

    @PutMapping("/alterar-saldo/{id}/{saldo}")
    public String alterarSaldo(@PathVariable("id") Long id, @PathVariable("saldo") Double saldo) {
        contaService.alterarSaldoConta(id, saldo);
        return "Saldo da conta alterada com sucesso!";
    }

}