package org.example.banco.selenium.pages;

import org.example.banco.selenium.core.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class AlterarContaPage extends BasePage {
    private Long id;
    private String URL;

    private By botaoAlterar = By.xpath("//button[@class='MuiButtonBase-root MuiButton-root MuiButton-contained MuiButton-containedPrimary MuiButton-sizeLarge MuiButton-containedSizeLarge MuiButton-colorPrimary MuiButton-root MuiButton-contained MuiButton-containedPrimary MuiButton-sizeLarge MuiButton-containedSizeLarge MuiButton-colorPrimary css-3puyd9-MuiButtonBase-root-MuiButton-root']");
    private By campoSaldo = By.name("saldo");
    private By caixaErro = By.xpath("//div[@class='MuiAlert-message css-zioonp-MuiAlert-message']");

    public AlterarContaPage(WebDriver driver, Long id) {
        super(driver);
        this.id = id;
        this.URL = "http://localhost:5173/contas/" + id + "/edit";
    }

    public AlterarContaPage abrir(){
        driver.get(URL);
        return this;
    }

    public AlterarContaPage preencherSaldo(String saldo) {
        type(campoSaldo, saldo);
        return this;
    }

    public AlterarContaPage preencherSaldoZero() {
        typeZero(campoSaldo);
        return this;
    }

    public ContasPage submeterAlteracao() {
        click(botaoAlterar);
        return new ContasPage(driver);
    }

    public AlterarContaPage submeterAlteracaoComErros() {
        click(botaoAlterar);
        return this;
    }

    public String lerErro() {
        return $(caixaErro).getText().isEmpty() ? "" : $(caixaErro).getText();
    }

    public AlterarContaPage esperarErroAparecer() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(caixaErro));
        return this;
    }

}