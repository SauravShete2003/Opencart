package testCases;

import org.testng.Assert;
import org.testng.annotations.Test;

import pageObjects.HomePage;
import pageObjects.LoginPage;
import pageObjects.MyAccountPage;
import utilities.DataProviders;

public class TC003_LoginDDT extends BaseClass {

	@Test(dataProvider = "LoginData", dataProviderClass = DataProviders.class , groups="Datadriven") // getting another class data provider
	public void verify_logout_DDT(String email, String pwd, String exp) {
		logger.info("****** Starting TC0003_LoginDDT********");
		try {
			HomePage hp = new HomePage(driver);
			hp.clickMyAccount();
			hp.clickLogin();
			logger.info("filling creditensial");
			LoginPage lp = new LoginPage(driver);
			lp.setEmail(email);
			lp.setPassword(pwd);
			lp.setLoginBtn();

			MyAccountPage my = new MyAccountPage(driver);
			boolean targetPage = my.isMyAccountPageExists();
			Assert.assertTrue(targetPage);

			if (exp.equalsIgnoreCase("Valid")) {
				if (targetPage == true) {
					my.clickLogout();
					Assert.assertTrue(true);
				} else {
					Assert.assertTrue(false);
				}
			}

			if (exp.equalsIgnoreCase("Invalid")) {
				if (targetPage == true) {
					my.clickLogout();
					Assert.assertTrue(false);
				} else {
					Assert.assertTrue(true);

				}
			}
		} catch (Exception e) {
			Assert.fail();
		}
		logger.info("****** Finished TC0003_LoginDDT********");

	}
}
