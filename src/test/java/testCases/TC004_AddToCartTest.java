package testCases;

import org.testng.Assert;
import org.testng.annotations.Test;

import pageObjects.AddToCartPage;

public class TC004_AddToCartTest extends BaseClass {

	@Test(groups = { "Sanity", "Master" })
	public void addToCart() {
		String product = "iPhone";
		logger.info("****** Starting Add To Cart page*****");
		try {
			AddToCartPage cart = new AddToCartPage(driver);
			cart.addToCartFromListing(product);
			Assert.assertTrue(cart.isSuccessMessageDisplayed());
			cart.openShoppingCart();
			Assert.assertTrue(cart.isProductInCart(product));
			System.out.println("URL: " + driver.getCurrentUrl());
			System.out.println("TITLE: " + driver.getTitle());
			System.out.println("CART HEADER: " + cart.getCartTotalText());
			Thread.sleep(5000);   // pauses so you can look at the browser
		} catch (Exception e) {
			e.printStackTrace(); // add this line
			logger.error("Test failed: " + e.getMessage());
			Assert.fail(e.getMessage());
		}
	}

}
