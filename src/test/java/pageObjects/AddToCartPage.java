package pageObjects;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AddToCartPage extends BasePage {
	WebDriverWait wait;

	public AddToCartPage(WebDriver driver) {
		super(driver);
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	// ===================== LOCATORS =====================

	// ---- Search ----
	@FindBy(name = "search")
	WebElement txtSearch;

	@FindBy(css = "#search button")
	WebElement btnSearch;

	// ---- Product page ----
	@FindBy(id = "input-quantity")
	WebElement txtQuantity;

	@FindBy(id = "button-cart")
	WebElement btnAddToCartProductPage;

	// ---- Success alert (shown after adding to cart) ----
	By successAlert = By.cssSelector("div.alert.alert-success");

	// ---- Header cart ----
	@FindBy(css = "#cart > button")
	WebElement btnCartDropdown;

	@FindBy(id = "cart-total")
	WebElement lblCartTotal;

	@FindBy(xpath = "//a[@title='Shopping Cart']")
	WebElement lnkShoppingCart;

	// ---- Shopping cart page ----
	@FindBy(xpath = "//div[@id='content']//p[contains(text(),'empty')]")
	WebElement lblEmptyCart;

	@FindBy(xpath = "//div[@class='buttons clearfix']//a[text()='Checkout']")
	WebElement btnCheckout;

	@FindBy(xpath = "//div[@class='buttons clearfix']//a[text()='Continue Shopping']")
	WebElement btnContinueShopping;

	// ---- Dynamic locators (built with the product name) ----
	private By listingAddToCartBtn(String productName) {
		return By.xpath("//div[contains(@class,'product-thumb')]//h4/a[normalize-space()='" + productName
				+ "']/ancestor::div[contains(@class,'product-thumb')]//button[contains(@onclick,'cart.add')]");
	}

	private By productLink(String productName) {
		return By.xpath("//div[contains(@class,'product-thumb')]//h4/a[normalize-space()='" + productName + "']");
	}

	private By cartRowProductName(String productName) {
		return By.xpath("//div[@id='content']//table//a[normalize-space()='" + productName + "']");
	}

	private By cartRowQuantityInput(String productName) {
		return By.xpath("//td[@class='text-left']/a[normalize-space()='" + productName
				+ "']/ancestor::tr//input[starts-with(@name,'quantity')]");
	}

	private By cartRowUpdateBtn(String productName) {
		return By.xpath("//td[@class='text-left']/a[normalize-space()='" + productName
				+ "']/ancestor::tr//button[@data-original-title='Update']");
	}

	private By cartRowRemoveBtn(String productName) {
		return By.xpath("//td[@class='text-left']/a[normalize-space()='" + productName
				+ "']/ancestor::tr//button[@data-original-title='Remove']");
	}

	// ===================== ACTIONS =====================

	// ---- Search ----
	public void searchProduct(String productName) {
		txtSearch.clear();
		txtSearch.sendKeys(productName);
		btnSearch.click();
	}

	// ---- Add to cart from listing (home page / search results / category) ----
	public void addToCartFromListing(String productName) {
		WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(listingAddToCartBtn(productName)));
		scrollIntoView(btn);
		btn.click();
	}

	// ---- Add to cart from product details page ----
	public void openProduct(String productName) {
		wait.until(ExpectedConditions.elementToBeClickable(productLink(productName))).click();
	}

	public void setQuantity(String qty) {
		txtQuantity.clear();
		txtQuantity.sendKeys(qty);
	}

	public void clickAddToCartOnProductPage() {
		scrollIntoView(btnAddToCartProductPage);
		btnAddToCartProductPage.click();
	}

	// ---- Success message ----
	public boolean isSuccessMessageDisplayed() {
		try {
			return wait.until(ExpectedConditions.visibilityOfElementLocated(successAlert)).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	public String getSuccessMessage() {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(successAlert)).getText();
	}

	// ---- Header cart ----
	public String getCartTotalText() {
		return lblCartTotal.getText(); // e.g. "1 item(s) - $602.00"
	}

	public int getCartItemCount() {
		String text = getCartTotalText();
		return Integer.parseInt(text.split(" ")[0]);
	}

	public void openCartDropdown() {
		btnCartDropdown.click();
	}

	// ---- Shopping cart page ----
	public void openShoppingCart() {
		scrollToTop();
		wait.until(ExpectedConditions.elementToBeClickable(lnkShoppingCart)).click();
	}

	public boolean isProductInCart(String productName) {
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(cartRowProductName(productName)));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String getProductQuantityInCart(String productName) {
		return driver.findElement(cartRowQuantityInput(productName)).getAttribute("value");
	}

	public void updateProductQuantity(String productName, String qty) {
		WebElement input = driver.findElement(cartRowQuantityInput(productName));
		input.clear();
		input.sendKeys(qty);
		driver.findElement(cartRowUpdateBtn(productName)).click();
	}

	public void removeProductFromCart(String productName) {
		driver.findElement(cartRowRemoveBtn(productName)).click();
	}

	public boolean isCartEmpty() {
		try {
			return wait.until(ExpectedConditions.visibilityOf(lblEmptyCart)).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	public String getEmptyCartMessage() {
		return lblEmptyCart.getText(); // "Your shopping cart is empty!"
	}

	public void clickCheckout() {
		btnCheckout.click();
	}

	public void clickContinueShopping() {
		btnContinueShopping.click();
	}

	// ===================== HELPERS =====================

	private void scrollIntoView(WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
	}

	private void scrollToTop() {
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
	}
}