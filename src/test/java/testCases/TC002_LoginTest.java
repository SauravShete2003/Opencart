package testCases;

import org.testng.Assert;
import org.testng.annotations.Test;

import pageObjects.HomePage;
import pageObjects.LoginPage;
import pageObjects.MyAccountPage;

public class TC002_LoginTest extends BaseClass {
	@Test(groups={"Sanity", "Master"})
	public void verify_Login() {
		logger.info("****** Starting login page*****");
		try {
			HomePage hp = new HomePage(driver);
			hp.clickMyAccount();
			hp.clickLogin();
			logger.info("filling creditensial");
			LoginPage lp = new LoginPage(driver);
			lp.setEmail(p.getProperty("email"));
			lp.setPassword(p.getProperty("password"));
			lp.setLoginBtn();
			
			MyAccountPage my= new MyAccountPage(driver);
			boolean targetpage=my.isMyAccountPageExists();
			Assert.assertTrue(targetpage);
		} catch (Exception e) {

			Assert.fail();
			logger.info("******* Finished TC002_LoginTest******");
		}
	}
}
