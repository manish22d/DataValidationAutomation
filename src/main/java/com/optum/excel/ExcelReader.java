package com.optum.excel;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class ExcelReader {
    XSSFWorkbook workbook;

    public ExcelReader(File dataFile) {

        try {
            workbook = new XSSFWorkbook(dataFile);
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

    public List<Map<String, String>> readFile(String sheetName) {
        XSSFSheet sheet = workbook.getSheet(sheetName);
        return getData(sheet);
    }

    private List<Map<String, String>> getData(XSSFSheet sheet) {
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        List<String> headers = getHeaders(sheet);
        DataFormatter formatter = new DataFormatter();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Map<String, String> rowMap = new HashedMap<String, String>();
            XSSFRow row = sheet.getRow(i);
            forEachWithCounter(row, (index, cell) -> {
                rowMap.put(headers.get(index), formatter.formatCellValue(cell));
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



}
