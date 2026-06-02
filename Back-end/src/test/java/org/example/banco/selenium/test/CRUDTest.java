package org.example.banco.selenium.test;

import org.example.banco.selenium.core.BaseTest;
import org.example.banco.selenium.pages.AlterarContaPage;
import org.example.banco.selenium.pages.ContasPage;
import org.example.banco.selenium.pages.VisualizarContaPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CRUDTest extends BaseTest {

    @Test
    public void obterListaDeContas() {
        ContasPage retorno = new ContasPage(driver)
                .abrir();
        Assertions.assertTrue(retorno.obterListaDeElementosConta().size() > 0);
    }

    @Test
    public void lerContaEscolhida() {
        VisualizarContaPage retorno = new ContasPage(driver)
                .abrir()
                .clicarPrimeiraConta()
                .esperarAteContaAparecer();
        Assertions.assertTrue(retorno.lerIdConta() != "");
    }

    @Test
    public void criarContaCorreta() {
        ContasPage retorno = new ContasPage(driver)
                .abrir()
                .clicarCriarNovaConta()
                .escreverNome("Adam Henry")
                .escreverSaldo("100")
                .submeterCriacaoConta()
                .esperarConfirmacao();
        Assertions.assertTrue(retorno.lerConfirmacao().contains("Item created successfully."));
    }

    @Test
    public void deletarUsuario() {
        ContasPage retorno = new ContasPage(driver)
                .abrir()
                .clicarDeletarPrimeiraConta()
                .clicarConfirmacaoDeDelecao()
                .esperarConfirmacao();
        Assertions.assertTrue(retorno.lerConfirmacao().contains("Item deleted successfully."));
    }

    @Test
    public void atualizarSaldoComValorAcimaDe0() {
        ContasPage retorno = new ContasPage(driver)
                .abrir()
                .clicarPrimeiraConta()
                .clicarEditarPrimeiraConta()
                .preencherSaldo("300")
                .submeterAlteracao()
                .esperarConfirmacao();
        Assertions.assertTrue(retorno.lerConfirmacao().contains("Item edited successfully."));
    }

    @Test
    public void atualizarSaldoComValorAbaixoDe1() {
        AlterarContaPage retorno = new ContasPage(driver)
                .abrir()
                .clicarPrimeiraConta()
                .clicarEditarPrimeiraConta()
                .preencherSaldoZero()
                .submeterAlteracaoComErros()
                .esperarErroAparecer();
        Assertions.assertTrue(retorno.lerErro().contains("Saldo deve ser maior que 0."));
    }

}