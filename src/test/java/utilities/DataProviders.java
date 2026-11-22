package utilities;

import java.io.IOException;

import org.testng.annotations.DataProvider;

public class DataProviders {

    // DataProvider 1
    @DataProvider(name = "LoginData")
    public String[][] getData() throws IOException {

    	String path = System.getProperty("user.dir") + "/testData/loginData.xlsx"; // taking xl file from testData

        ExcelUtility xlutil = new ExcelUtility(path); // creating an object for ExcelUtility

        int totalrows = xlutil.getDataRowCount("Sheet1"); // data rows only (no header)
        int totalcols = xlutil.getCellCount("Sheet1", 0); // columns, counted from the header row

        String logindata[][] = new String[totalrows][totalcols]; // two dimension array to store the data

        for (int i = 1; i <= totalrows; i++) { // read the data from xl, starting at row 1 (row 0 is the header)
            for (int j = 0; j < totalcols; j++) { // i is row, j is col
                logindata[i - 1][j] = xlutil.getCellData("Sheet1", i, j);
            }
        }
        return logindata; // returning two dimension array
    }

    // DataProvider 2
    // (add more providers here the same way)
}