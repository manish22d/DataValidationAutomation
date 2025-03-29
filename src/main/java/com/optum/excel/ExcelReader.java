package com.optum.excel;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

//@Component
public class ExcelReader {
    XSSFWorkbook workbook;

    public ExcelReader(Path dataFile) {
        File file = new File(dataFile.toUri());
        try {
            workbook = new XSSFWorkbook(file);
        } catch (IOException | InvalidFormatException e) {
            throw new RuntimeException(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                workbook.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));

    }

    private static Object evaluateFormulaCell(Cell cell) {
        try {
            FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
            CellValue cellValue = evaluator.evaluate(cell);

            return switch (cellValue.getCellType()) {
                case STRING -> cellValue.getStringValue();
                case NUMERIC -> cellValue.getNumberValue();
                case BOOLEAN -> cellValue.getBooleanValue();
                default -> null;
            };
        } catch (Exception e) {
            return "FORMULA_ERROR";
        }
    }

    public List<Map<String, Object>> readFile(String sheetName) {
        XSSFSheet sheet = workbook.getSheet(sheetName);
        return getData(sheet);
    }

    private List<Map<String, Object>> getData(XSSFSheet sheet) {
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        List<String> headers = getHeaders(sheet);
        DataFormatter formatter = new DataFormatter();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Map<String, Object> rowMap = new HashedMap<String, Object>();
            XSSFRow row = sheet.getRow(i);
            forEachWithCounter(row, (index, cell) -> {
                rowMap.put(headers.get(index), getCellValue(cell));
            });
            data.add(rowMap);
        }
        return Collections.unmodifiableList(data);
    }

    private List<String> getHeaders(XSSFSheet sheet) {
        List<String> headers = new ArrayList<String>();
        XSSFRow row = sheet.getRow(0);
        row.forEach((cell) -> {
            headers.add(cell.getStringCellValue());
        });
        return Collections.unmodifiableList(headers);
    }

    private void forEachWithCounter(Iterable<Cell> source, BiConsumer<Integer, Cell> biConsumer) {
        int i = 0;
        for (Cell cell : source) {
            biConsumer.accept(i, cell);
            i++;
        }
    }

    private Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();

            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue(); // Returns Date object if formatted as a date
                }
                double numericValue = cell.getNumericCellValue();
                if (numericValue % 1 == 0) { // Check if it's an integer
                    return (int) numericValue;
                }
                return numericValue;
            case BOOLEAN:
                return cell.getBooleanCellValue();

            case FORMULA:
                return evaluateFormulaCell(cell); // Custom method to evaluate formulas

            case BLANK:
                return "";

            case ERROR:
                return FormulaError.forInt(cell.getErrorCellValue()).getString();

            default:
                return null;
        }
    }

}
