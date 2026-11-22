package utilities;

import java.nio.file.Files;

import java.nio.file.Paths;
import java.util.Base64;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import testCases.BaseClass; // change to testBase.BaseClass if that is where your BaseClass lives

public class ExtentReportUtility implements ITestListener {

	public ExtentSparkReporter sparkReporter;
	public ExtentReports extent;
	public ExtentTest test;

	String repName;

	@Override
	public void onStart(ITestContext testContext) {

		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()); // time stamp
		repName = "Test-Report-" + timeStamp + ".html";

		sparkReporter = new ExtentSparkReporter(".\\reports\\" + repName); // location of the report

		sparkReporter.config().setDocumentTitle("opencart Automation Report"); // title of report
		sparkReporter.config().setReportName("opencart Functional Testing"); // name of the report
		sparkReporter.config().setTheme(Theme.DARK);

		extent = new ExtentReports();
		extent.attachReporter(sparkReporter);
		extent.setSystemInfo("Application", "opencart");
		extent.setSystemInfo("Module", "Admin");
		extent.setSystemInfo("Sub Module", "Customers");
		extent.setSystemInfo("User Name", System.getProperty("user.name"));
		extent.setSystemInfo("Environment", "QA");

		// values come from the <parameter> tags in testng.xml
		String os = testContext.getCurrentXmlTest().getParameter("os");
		if (os != null) {
			extent.setSystemInfo("Operating System", os);
		}

		String browser = testContext.getCurrentXmlTest().getParameter("browser");
		if (browser != null) {
			extent.setSystemInfo("Browser", browser);
		}

		List<String> includedGroups = testContext.getCurrentXmlTest().getIncludedGroups();
		if (!includedGroups.isEmpty()) {
			extent.setSystemInfo("Groups", includedGroups.toString());
		}
	}

	@Override
	public void onTestSuccess(ITestResult result) {

		test = extent.createTest(result.getTestClass().getName());
		test.assignCategory(result.getMethod().getGroups()); // to display groups in report
		test.log(Status.PASS, result.getName() + " got successfully executed");
	}

	@Override
	public void onTestFailure(ITestResult result) {

		test = extent.createTest(result.getTestClass().getName());
		test.assignCategory(result.getMethod().getGroups());

		test.log(Status.FAIL, result.getName() + " got failed");
		if (result.getThrowable() != null) {
			test.log(Status.INFO, result.getThrowable().getMessage());
		}

		try {
			String imgPath = new BaseClass().captureScreen(result.getName());
			byte[] imgBytes = Files.readAllBytes(Paths.get(imgPath));
			String base64 = Base64.getEncoder().encodeToString(imgBytes);
			test.addScreenCaptureFromBase64String(base64, result.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTestSkipped(ITestResult result) {

		test = extent.createTest(result.getTestClass().getName());
		test.assignCategory(result.getMethod().getGroups());
		test.log(Status.SKIP, result.getName() + " got skipped");
		if (result.getThrowable() != null) {
			test.log(Status.INFO, result.getThrowable().getMessage());
		}
	}

	@Override
	public void onFinish(ITestContext testContext) {

		extent.flush(); // writes everything to the HTML file

		// open the report automatically in the default browser
		String pathOfExtentReport = System.getProperty("user.dir") + "\\reports\\" + repName;
		File extentReport = new File(pathOfExtentReport);

		try {
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().browse(extentReport.toURI());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		 * try { URL url = new URL("file:///" + System.getProperty("user.dir") +
		 * "\\reports\\" + repName);
		 * 
		 * // Create the email message ImageHtmlEmail email = new ImageHtmlEmail();
		 * 
		 * email.setDataSourceResolver(new DataSourceUrlResolver(url));
		 * email.setHostName("smtp.gmail.com"); email.setSmtpPort(465);
		 * 
		 * email.setAuthenticator( new DefaultAuthenticator("your-email@gmail.com",
		 * "your-app-password") );
		 * 
		 * email.setSSLOnConnect(true); email.setFrom("your-email@gmail.com"); // Sender
		 * email.setSubject("Test Results");
		 * email.setMsg("Please find Attached Report....");
		 * 
		 * email.addTo("receiver@gmail.com"); // Receiver
		 * 
		 * email.attach(url, "extent report", "please check report...");
		 * 
		 * email.send(); // Send the email } catch (Exception e) { e.printStackTrace();
		 * }
		 */
	}
}