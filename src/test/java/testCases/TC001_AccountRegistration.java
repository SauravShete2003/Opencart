package testCases;
import org.testng.Assert;
import org.testng.annotations.Test;
import pageObjects.AccountRegistraction;
import pageObjects.HomePage;

public class TC001_AccountRegistration extends BaseClass{

	@Test(groups={"Regression", "Master"})
	public void verifyAccountRegistreation() {
		logger.info("****** Starting TC001_AccountRegistration ******");
		try {
		HomePage hp = new HomePage(driver);
		hp.clickMyAccount();
		logger.info("Logged on MyAccount Link");
		hp.clickRegister();
		logger.info("Logged on Register Link");

		AccountRegistraction repage = new AccountRegistraction(driver);
		logger.info("Providing customer details");
		repage.setFirstName(randomString().toUpperCase());
		repage.setLastName(randomString().toUpperCase());
		repage.setEmail(randomString() + "@gmail.com");

		repage.setPhone(randomNumber());
		
		String password=randomAlphabate();
		
		repage.setPassword(password);
		repage.setPasswordConfirm(password);
		repage.checkBox();
		repage.setRegisterBtn();
		logger.info("Validating expected message");
		}catch(Exception e){
			logger.error("Test Failed");
			logger.debug("Debug logs");
			Assert.fail();
		}

	}

}