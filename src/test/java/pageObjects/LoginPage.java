package pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage{
	public LoginPage(WebDriver driver) {
		super(driver);
	}
	
	@FindBy(id="input-email")
	WebElement emailInput;
	
	@FindBy(id="input-password")
	WebElement passwordInput;
	
	@FindBy(xpath="//input[@type='submit']")
	WebElement loginBtn;
	
	public void setEmail(String email) {
		emailInput.sendKeys(email);
	}
	
	public void setPassword(String password) {
		passwordInput.sendKeys(password);
	}
	
	public void setLoginBtn() {
		loginBtn.click();
	}
}
