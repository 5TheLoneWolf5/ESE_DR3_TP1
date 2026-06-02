package org.example.banco.selenium.pages;

import org.example.banco.selenium.core.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class VisualizarContaPage extends BasePage {

    private Long id;
    private String URL;
    private By campoID = By.xpath("//p[@class='MuiTypography-root MuiTypography-body1 css-lkr96q-MuiTypography-root']");
    private By botaoEditar = By.xpath("//button[@class='MuiButtonBase-root MuiButton-root MuiButton-contained MuiButton-containedPrimary MuiButton-sizeMedium MuiButton-containedSizeMedium MuiButton-colorPrimary MuiButton-root MuiButton-contained MuiButton-containedPrimary MuiButton-sizeMedium MuiButton-containedSizeMedium MuiButton-colorPrimary css-1xiksjx-MuiButtonBase-root-MuiButton-root']");

    public VisualizarContaPage(WebDriver driver, Long id) {
        super(driver);
        this.id = id;
        this.URL = "http://localhost:5173/contas/" + id;
    }
    public VisualizarContaPage abrir(){
        driver.get(URL);
        return this;
    }

    public String lerIdConta(){
        return $(campoID).getText().isEmpty() ? "" : $(campoID).getText();
    }

    public VisualizarContaPage esperarAteContaAparecer(){
        wait.until(ExpectedConditions.visibilityOfElementLocated(campoID));
        return this;
    }

    public AlterarContaPage clicarEditarPrimeiraConta() {;
        click(botaoEditar);

        AlterarContaPage alterarContaPage = new AlterarContaPage(driver, id);
        return alterarContaPage;
    }
}
