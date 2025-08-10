package utils;

import data.enums.sia.AdOns;
import data.enums.sia.Plans;
import data.models.leapfrog.GameInfo;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import testdata.SIATestData;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import static common.Constants.DATA_DRIVEN_OUTPUT;
import static common.Constants.LEAP_FROG_EXCEL_PATH;
import static common.Constants.SIA_EXCEL_PATH;

public class ExcelUtils {

    private static final Object excelLock = new Object();

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
            if (sheet.getPhysicalNumberOfRows() < 2)
                throw new IllegalStateException("Excel file is empty or missing data rows.");

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

    public static List<SIATestData> loadSIATestData() {
        clearSIAResult(); // clear previous results before loading new data
        List<SIATestData> dataList = new ArrayList<>();

        try (Reader in = new FileReader(SIA_EXCEL_PATH)) {
            List<CSVRecord> records = CSVFormat.DEFAULT.parse(in).getRecords();
            if (records.size() < 2)
                throw new IllegalStateException("CSV does not contain enough rows for page indicator and header.");

            // build a column index map from header row (2nd row, index 1)
            HashMap<String, Integer> columnIndexMap = extractColumnIndicesCSV(records);

            // iterate from the third row (index 2) onwards
            for (int index = 2; index < records.size(); index++) {
                CSVRecord record = records.get(index);
                SIATestData data = fromCSVRecord(record, columnIndexMap);

                dataList.add(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV: " + SIA_EXCEL_PATH, e);
        }
        return dataList;
    }

    public static void recordSIAOutcome(String actualPrice, int dataNum) {
        if (!DATA_DRIVEN_OUTPUT) return;

        updateSIARecords((row, columnIndexMap) -> {
            if (actualPrice != null && Integer.parseInt(row[columnIndexMap.get("no.")].trim()) == dataNum) {
                row[columnIndexMap.get("actual price")] = actualPrice;
                String expectedPrice = row[columnIndexMap.get("expected price")].trim();
                row[columnIndexMap.get("result")] = actualPrice.equals(expectedPrice) ? "match" : "mismatch";
            }
        });
    }

    private static void clearSIAResult() {
        updateSIARecords((row, columnIndexMap) -> {
            row[columnIndexMap.get("actual price")] = "";
            row[columnIndexMap.get("result")] = "";
        });
    }

    private static void updateSIARecords(BiConsumer<String[], HashMap<String, Integer>> updater) {
        synchronized (excelLock) {
            try {
                List<CSVRecord> records = CSVFormat.DEFAULT.parse(new FileReader(SIA_EXCEL_PATH)).getRecords();
                HashMap<String, Integer> columnIndexMap = extractColumnIndicesCSV(records);

                List<String[]> updatedRows = new ArrayList<>();
                for (int index = 0; index < records.size(); index++) {
                    CSVRecord record = records.get(index);
                    String[] row = record.stream().toArray(String[]::new);

                    // skip page indicator and header
                    if (index >= 2) updater.accept(row, columnIndexMap);

                    updatedRows.add(row);
                }

                writeToCSV(updatedRows, SIA_EXCEL_PATH);

            } catch (Exception e) {
                throw new RuntimeException("Error updating CSV file", e);
            }
        }
    }

    private static SIATestData fromCSVRecord(CSVRecord record, HashMap<String, Integer> columnIndexMap) {
        SIATestData data = new SIATestData();
        data.setRowNum(Integer.parseInt(record.get(columnIndexMap.get("no.")).trim()));
        data.setDepartureLocation(record.get(columnIndexMap.get("departure")).trim());
        data.setDestinationLocation(record.get(columnIndexMap.get("destination")).trim());
        data.setTripLong(Integer.parseInt(record.get(columnIndexMap.get("trip long")).trim()));
        data.setTravellers(Integer.parseInt(record.get(columnIndexMap.get("travellers")).trim()));

        // parse ages as space-separated integers
        int[] ages = Arrays.stream(record.get(columnIndexMap.get("ages")).split(" "))
                           .map(String::trim).mapToInt(Integer::parseInt).toArray();
        if (data.getTravellers() != ages.length)
            throw new IllegalStateException("Number of travellers does not match number of ages provided.");
        data.setAges(ages);

        data.setPlan(Plans.valueOf(record.get(columnIndexMap.get("plans")).trim().toUpperCase()));
        data.setAddOns(AdOns.valueOf(record.get(columnIndexMap.get("ad-ons")).trim().toUpperCase()));
        data.setPrice(record.get(columnIndexMap.get("expected price")).trim());

        return data;
    }

    private static void writeToCSV(List<String[]> updatedRows, String filePath) {
        try (PrintWriter writer = new PrintWriter(filePath)) {
            for (String[] row : updatedRows) {
                for (int i = 0; i < row.length; i++) {
                    String cellValue = row[i];
                    writer.print(cellValue);
                    if (i < row.length - 1) writer.print(",");
                }
                writer.println();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error writing CSV file", e);
        }
    }

    private static HashMap<String, Integer> extractColumnIndicesCSV(List<CSVRecord> records) {
        CSVRecord headerRow = records.get(1);
        HashMap<String, Integer> columnIndexMap = new HashMap<>();
        IntStream.range(0, headerRow.size())
                 .forEach(i -> columnIndexMap.put(headerRow.get(i).trim().toLowerCase(), i));

        return columnIndexMap;
    }

    private static HashMap<String, Integer> extractColumnIndices(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        HashMap<String, Integer> columnIndexMap = new HashMap<>();
        IntStream.range(0, headerRow.getPhysicalNumberOfCells())
                 .forEach(i -> columnIndexMap.put(getCellValue(headerRow.getCell(i)).toLowerCase(), i));
        return columnIndexMap;
    }

    private static GameInfo parseRow(Row row, HashMap<String, Integer> columnIndexMap) {
        String title = getCellValue(row.getCell(columnIndexMap.get("title")));
        String ageRange = getCellValue(row.getCell(columnIndexMap.get("age")));
        String price = getCellValue(row.getCell(columnIndexMap.get("price")));

        return new GameInfo(row.getRowNum(), title, ageRange, price);
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            default -> "";
        };
    }
}

