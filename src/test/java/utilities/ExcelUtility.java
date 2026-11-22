package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Reusable Excel (.xlsx) utility using Apache POI.
 * Stateless: every call opens and closes the file, so it is safe to share.
 * Row 0 is treated as the header row for the header-based methods.
 */
public class ExcelUtility {

    private final String path;
    private final DataFormatter formatter = new DataFormatter();

    public ExcelUtility(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Excel file path cannot be null or empty");
        }
        this.path = path;
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private Workbook openWorkbook() throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("Excel file not found: " + path);
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            return new XSSFWorkbook(fis);
        }
    }

    private Sheet getSheet(Workbook wb, String sheetName) {
        Sheet sheet = wb.getSheet(sheetName);
        if (sheet == null) {
            throw new IllegalArgumentException("Sheet not found: " + sheetName);
        }
        return sheet;
    }

    private String readCell(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) {
            return "";
        }
        return formatter.formatCellValue(cell, evaluator);
    }

    private int findColumnIndex(Sheet sheet, String columnName) {
        Row header = sheet.getRow(0);
        if (header == null) {
            throw new IllegalArgumentException("Header row (row 0) is empty in sheet: " + sheet.getSheetName());
        }
        for (Cell cell : header) {
            if (formatter.formatCellValue(cell).trim().equalsIgnoreCase(columnName.trim())) {
                return cell.getColumnIndex();
            }
        }
        throw new IllegalArgumentException("Column not found: " + columnName);
    }

    // ---------------------------------------------------------------
    // Counts
    // ---------------------------------------------------------------

    /** Total rows including the header row. */
    public int getRowCount(String sheetName) throws IOException {
        try (Workbook wb = openWorkbook()) {
            return getSheet(wb, sheetName).getLastRowNum() + 1;
        }
    }

    /** Data rows only (excludes the header row). */
    public int getDataRowCount(String sheetName) throws IOException {
        return Math.max(getRowCount(sheetName) - 1, 0);
    }

    /** Number of cells in the given row. */
    public int getCellCount(String sheetName, int rowNum) throws IOException {
        try (Workbook wb = openWorkbook()) {
            Row row = getSheet(wb, sheetName).getRow(rowNum);
            if (row == null || row.getLastCellNum() < 0) {
                return 0;
            }
            return row.getLastCellNum();
        }
    }

    // ---------------------------------------------------------------
    // Read
    // ---------------------------------------------------------------

    /** Read a cell by row and column index (0-based). Returns "" for empty cells. */
    public String getCellData(String sheetName, int rowNum, int colNum) throws IOException {
        try (Workbook wb = openWorkbook()) {
            Sheet sheet = getSheet(wb, sheetName);
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                return "";
            }
            FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
            return readCell(row.getCell(colNum), evaluator);
        }
    }

    /** Read a cell by row index and header name (case-insensitive). */
    public String getCellData(String sheetName, int rowNum, String columnName) throws IOException {
        try (Workbook wb = openWorkbook()) {
            Sheet sheet = getSheet(wb, sheetName);
            int colNum = findColumnIndex(sheet, columnName);
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                return "";
            }
            FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
            return readCell(row.getCell(colNum), evaluator);
        }
    }

    /**
     * Whole sheet as Object[][] for TestNG @DataProvider.
     * Skips the header row and completely blank rows.
     */
    public Object[][] getSheetData(String sheetName) throws IOException {
        try (Workbook wb = openWorkbook()) {
            Sheet sheet = getSheet(wb, sheetName);
            FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

            Row header = sheet.getRow(0);
            if (header == null || header.getLastCellNum() <= 0) {
                return new Object[0][0];
            }
            int colCount = header.getLastCellNum();

            List<Object[]> rows = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                String[] data = new String[colCount];
                boolean blank = true;
                for (int j = 0; j < colCount; j++) {
                    data[j] = readCell(row.getCell(j), evaluator);
                    if (!data[j].isEmpty()) {
                        blank = false;
                    }
                }
                if (!blank) {
                    rows.add(data);
                }
            }
            return rows.toArray(new Object[0][]);
        }
    }

    /** Whole sheet as a list of maps (header name -> value). Skips blank rows. */
    public List<Map<String, String>> getSheetDataAsMap(String sheetName) throws IOException {
        try (Workbook wb = openWorkbook()) {
            Sheet sheet = getSheet(wb, sheetName);
            FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

            List<Map<String, String>> result = new ArrayList<>();
            Row header = sheet.getRow(0);
            if (header == null || header.getLastCellNum() <= 0) {
                return result;
            }
            int colCount = header.getLastCellNum();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                Map<String, String> map = new LinkedHashMap<>();
                boolean blank = true;
                for (int j = 0; j < colCount; j++) {
                    String key = formatter.formatCellValue(header.getCell(j)).trim();
                    String value = readCell(row.getCell(j), evaluator);
                    map.put(key, value);
                    if (!value.isEmpty()) {
                        blank = false;
                    }
                }
                if (!blank) {
                    result.add(map);
                }
            }
            return result;
        }
    }

    // ---------------------------------------------------------------
    // Write
    // ---------------------------------------------------------------

    /**
     * Write a value to a cell. Creates the file, sheet, row and cell if missing.
     * Synchronized so parallel tests do not corrupt the file.
     * Throws IOException on failure (e.g. file open in Excel) instead of hiding it.
     */
    public synchronized boolean setCellData(String sheetName, int rowNum, int colNum, String data)
            throws IOException {

        File file = new File(path);
        File parent = file.getAbsoluteFile().getParentFile();
        if (!file.exists() && parent != null) {
            parent.mkdirs();
        }

        try (Workbook wb = file.exists() ? openWorkbook() : new XSSFWorkbook()) {
            Sheet sheet = wb.getSheet(sheetName);
            if (sheet == null) {
                sheet = wb.createSheet(sheetName);
            }
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                row = sheet.createRow(rowNum);
            }
            Cell cell = row.getCell(colNum);
            if (cell == null) {
                cell = row.createCell(colNum);
            }
            cell.setCellValue(data);

            // Input stream is already closed here, so it is safe to write to the same file
            try (FileOutputStream fos = new FileOutputStream(file)) {
                wb.write(fos);
            }
        }
        return true;
    }

    /** Write by header name (the column must already exist in row 0). */
    public synchronized boolean setCellData(String sheetName, int rowNum, String columnName, String data)
            throws IOException {
        int colNum;
        try (Workbook wb = openWorkbook()) {
            colNum = findColumnIndex(getSheet(wb, sheetName), columnName);
        }
        return setCellData(sheetName, rowNum, colNum, data);
    }
}