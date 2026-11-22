package pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class AccountRegistraction extends BasePage {

	public AccountRegistraction(WebDriver driver) {
		super(driver);
	}

	// locators

	@FindBy(id = "input-firstname")
	WebElement firstName;

	@FindBy(id = "input-lastname")
	WebElement lastName;

	@FindBy(id = "input-email")
	WebElement Email;

	@FindBy(id = "input-telephone")
	WebElement phone;

	@FindBy(id = "input-password")
	WebElement password;

	@FindBy(id = "input-confirm")
	WebElement passwordConfirm;

	@FindBy(xpath="//input[@type='submit']")
	WebElement registerBtn;
	
	@FindBy(xpath="//input[@type='checkbox']")
	WebElement privancyPolicayBtn;

	// Actions
	
	public void setFirstName(String fName) {
		firstName.sendKeys(fName);
	}

	public void setLastName(String lName) {
		lastName.sendKeys(lName);
	}
	
	public void setEmail(String email) {
		Email.sendKeys(email);
	}
	public void setPhone(String mobile) {
		phone.sendKeys(mobile);
	}

	public void setPassword(String pass) {
		password.sendKeys(pass);
	}
	
	public void setPasswordConfirm(String passConfirm) {
		passwordConfirm.sendKeys(passConfirm);
	}
	
	public void setRegisterBtn() {
		registerBtn.click();
	}
	
	public void checkBox() {
		privancyPolicayBtn.click();
	}
}
