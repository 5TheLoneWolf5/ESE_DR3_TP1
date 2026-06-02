package org.example.banco.selenium.pages;

import org.example.banco.selenium.core.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class NovaContaPage extends BasePage {
    private static final String url = "http://localhost:5173/contas/new";
    private final By botaoCriar = By.xpath("//button[@class='MuiButtonBase-root MuiButton-root MuiButton-contained MuiButton-containedPrimary MuiButton-sizeLarge MuiButton-containedSizeLarge MuiButton-colorPrimary MuiButton-root MuiButton-contained MuiButton-containedPrimary MuiButton-sizeLarge MuiButton-containedSizeLarge MuiButton-colorPrimary css-3puyd9-MuiButtonBase-root-MuiButton-root']");
    private final By campoNome = By.name("nome");
    private final By campoSaldo = By.name("saldo");


    public NovaContaPage(WebDriver driver) {
        super(driver);
    }

    public NovaContaPage abrir() {
        driver.get(url);
        return this;
    }

    public NovaContaPage escreverNome(String _nome) {
        type(campoNome, _nome);
        return this;
    }

    public NovaContaPage escreverSaldo(String _saldo) {
        type(campoSaldo, _saldo);
        return this;
    }

    public ContasPage submeterCriacaoConta() {
        click(botaoCriar);
        return new ContasPage(driver);
    }
}