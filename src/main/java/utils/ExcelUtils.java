package utils;

import data.models.leapfrog.GameInfo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.HashMap;

import static common.Constants.LEAP_FROG_EXCEL_PATH;

public class ExcelUtils {

    /**
     * reads the LeapFrog game information from the Excel file located at {@link common.Constants#LEAP_FROG_EXCEL_PATH}
     * <p>
     * each non-header row in the first sheet of the Excel file is mapped to a {@link GameInfo} object
     * the resulting map contains 1-based row indices as keys and corresponding {@link GameInfo} objects as values
     * </p>
     *
     * @return a {@link HashMap} where each key is the 1-based row index and the value is a {@link GameInfo} extracted from that row
     * @throws RuntimeException if there is an error reading the Excel file or if the file is empty or malformed
     */
    public static HashMap<Integer, GameInfo> getLeapFrogGameInfo() {
        HashMap<Integer, GameInfo> gameMap = new HashMap<>();

        try (
                FileInputStream input = new FileInputStream(LEAP_FROG_EXCEL_PATH);
                Workbook workbook = new XSSFWorkbook(input)
        ) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() < 2) {
                throw new IllegalStateException("Excel file is empty or missing data rows.");
            }

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // skip header row

                String title = getCellValue(row.getCell(0, MissingCellPolicy.RETURN_BLANK_AS_NULL));
                String ageRange = getCellValue(row.getCell(1, MissingCellPolicy.RETURN_BLANK_AS_NULL));
                String price = getCellValue(row.getCell(2, MissingCellPolicy.RETURN_BLANK_AS_NULL));

                GameInfo gameInfo = new GameInfo(title, ageRange, price);
                gameMap.put(row.getRowNum() + 1, gameInfo); // 1-based index
            }

        } catch (Exception e) {
            throw new RuntimeException("Error reading Excel file", e);
        }

        return gameMap;
    }

    private static String getCellValue(Cell cell) {
        return (cell == null) ? "" : cell.getStringCellValue().trim();
    }
}
