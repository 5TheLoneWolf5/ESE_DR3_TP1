package org.example.banco.selenium.pages;

import org.example.banco.selenium.core.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class ContasPage extends BasePage {
    private static final String URL = "http://localhost:5173/contas";
    private final By containerContas =  By.xpath("//div[@class='MuiDataGrid-virtualScrollerRenderZone css-11dqcl8-MuiDataGrid-virtualScrollerRenderZone']");
    private final By idContaLinha =  By.xpath("//div[@class='MuiDataGrid-cell MuiDataGrid-cell--textLeft']");
    private final By caixaConfirmacao = By.xpath("//div[@class='MuiAlert-message css-zioonp-MuiAlert-message']");
    private final By botaoCriar = By.xpath("//button[@class='MuiButtonBase-root MuiButton-root MuiButton-contained MuiButton-containedPrimary MuiButton-sizeMedium MuiButton-containedSizeMedium MuiButton-colorPrimary MuiButton-root MuiButton-contained MuiButton-containedPrimary MuiButton-sizeMedium MuiButton-containedSizeMedium MuiButton-colorPrimary css-1xiksjx-MuiButtonBase-root-MuiButton-root']");
    private final By confirmacaoDelecao = By.xpath("//button[@class='MuiButtonBase-root MuiButton-root MuiButton-text MuiButton-textError MuiButton-sizeMedium MuiButton-textSizeMedium MuiButton-colorError MuiButton-root MuiButton-text MuiButton-textError MuiButton-sizeMedium MuiButton-textSizeMedium MuiButton-colorError css-1x3jzn9-MuiButtonBase-root-MuiButton-root']");

    public ContasPage(WebDriver driver) {
        super(driver);
    }

    public ContasPage abrir(){
        driver.get(URL);
        return this;
    }

    public List<WebElement> obterListaDeElementosConta() {
        return $(containerContas).findElements(By.xpath(("./*")));
    }

    public VisualizarContaPage clicarPrimeiraConta() {
        WebElement contaBotao = obterListaDeElementosConta().getFirst();
        contaBotao.click();

        Long id = Long.parseLong(contaBotao.findElement(idContaLinha).getText());
        VisualizarContaPage visualizarContaPage = new VisualizarContaPage(driver, id);
        visualizarContaPage.abrir(); // É necessário abrir a página manualmente por conta da natureza SPA do front-end React.

        return visualizarContaPage;
    }

    public String lerConfirmacao() {
        return $(caixaConfirmacao).getText().isEmpty() ? "" : $(caixaConfirmacao).getText();
    }

    public ContasPage esperarConfirmacao() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(caixaConfirmacao));
        return this;
    }

    public ContasPage clicarConfirmacaoDeDelecao() {
        click(confirmacaoDelecao);
        return this;
    }

    public NovaContaPage clicarCriarNovaConta() {
        click(botaoCriar);
        NovaContaPage novaContaPage = new NovaContaPage(driver);

        return novaContaPage;
    }

    public ContasPage clicarDeletarPrimeiraConta() {
        WebElement conta = obterListaDeElementosConta().getFirst();
        WebElement deletarBotao = conta.findElement
                (By.xpath("//button[@aria-label='Delete']"));

        deletarBotao.click();
        return this;
    }

}