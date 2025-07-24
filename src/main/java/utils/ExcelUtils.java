package utils;

import data.models.leapfrog.GameInfo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static common.Constants.LEAP_FROG_EXCEL_PATH;

public class ExcelUtils {

    /**
     * reads the LeapFrog game information from the Excel file located at {@link common.Constants#LEAP_FROG_EXCEL_PATH}
     * <p>
     * each non-header row in the first sheet of the Excel file is mapped to a {@link GameInfo} object
     * the resulting list contains {@link GameInfo} objects with their index set to the 1-based row index
     * </p>
     *
     * @return a {@link List} of {@link GameInfo} extracted from the Excel file
     * @throws RuntimeException if there is an error reading the Excel file or if the file is empty or malformed
     */
    public static List<GameInfo> getLeapFrogGameInfo() {
        List<GameInfo> gameList = new ArrayList<>();

        try (
                FileInputStream input = new FileInputStream(LEAP_FROG_EXCEL_PATH);
                Workbook workbook = new XSSFWorkbook(input)
        ) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() < 2) {
                throw new IllegalStateException("Excel file is empty or missing data rows.");
            }

            HashMap<String, Integer> columnIndexMap = extractColumnIndices(sheet);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // skip header row

                GameInfo gameInfo = parseRow(row, columnIndexMap);
                gameInfo.setIndex(row.getRowNum() + 1); // 1-based index
                gameList.add(gameInfo);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error reading Excel file", e);
        }

        return gameList;
    }

    private static HashMap<String, Integer> extractColumnIndices(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        HashMap<String, Integer> columnIndexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            String header = getCellValue(cell).toLowerCase();
            columnIndexMap.put(header, cell.getColumnIndex());
        }
        return columnIndexMap;
    }

    private static GameInfo parseRow(Row row, HashMap<String, Integer> columnIndexMap) {
        String title = getCellValue(row.getCell(columnIndexMap.get("title"), MissingCellPolicy.RETURN_BLANK_AS_NULL));
        String ageRange = getCellValue(row.getCell(columnIndexMap.get("age"), MissingCellPolicy.RETURN_BLANK_AS_NULL));
        String price = getCellValue(row.getCell(columnIndexMap.get("price"), MissingCellPolicy.RETURN_BLANK_AS_NULL));
        return new GameInfo(row.getRowNum(), title, ageRange, price);
    }

    private static String getCellValue(Cell cell) {
        return cell.getStringCellValue().trim();
    }
}
