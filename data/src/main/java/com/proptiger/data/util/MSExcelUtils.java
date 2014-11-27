package com.proptiger.data.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.proptiger.core.exception.ProAPIException;

@Component
public class MSExcelUtils {

    private static Logger logger = LoggerFactory.getLogger(MSExcelUtils.class);

    public File exportWorkBookToFile(Workbook workbook, String wbFileName) {

        logger.debug("PnA_Report: Export workbook to file : Start.");

        FileOutputStream fileOutputStream = null;
        File wbFile = new File(wbFileName);
        try {
            wbFile.createNewFile();
            fileOutputStream = new FileOutputStream(wbFile);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            logger.debug("PnA_Report: Export workbook to file : Completed.");
        }
        catch (IOException ex) {
            throw new ProAPIException("Unable to save workbook to file.", ex);
        }

        return wbFile;
    }

    /**
     * 
     * Generates MS-Excel sheet from a given data. Ensures type consistency
     * across a column.
     * 
     * @param workBookName
     * @param sheetName
     *            : If a sheet with this name exists in the workbook then it
     *            will be overwritten, otherwise a new sheet will be added.
     * @param columnHeadings
     *            : List of objects arrays of the from [name, Class]
     * @param data
     *            : List containing row data. Each element of this list is a
     *            list-of-objects. Excess objects will be ignored and deficient
     *            lists will be packed will null values.
     */
    public Workbook exportToMsExcelWorkbook(
            String workBookName,
            String sheetName,
            List<Object[]> columnHeadings,
            List<List<Object>> data) throws ProAPIException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        if (columnHeadings == null || columnHeadings.isEmpty()) {
            throw new ProAPIException("No column headings specified while creating sheet");
        }

        if (data == null) {
            throw new ProAPIException("Null data given while creating sheet");
        }

        /* Fill first row : Heading */
        addColHeadingsToSheet(workbook, sheet, columnHeadings);

        /* Fill Data */
        fillWorkBookSheetWithData(workbook, sheet, columnHeadings, data);

        return workbook;
    }

    public void appendToMsExcelSheet(
            Workbook workbook,
            Sheet sheet,
            List<Object[]> columnHeadings,
            List<List<Object>> data) throws ProAPIException {

        if (workbook == null || sheet == null || data == null) {
            throw new ProAPIException("Null workbook, sheet or data given while creating sheet");
        }

        if (sheet.getLastRowNum() == 0) {
            addColHeadingsToSheet(workbook, sheet, columnHeadings);
        }
        fillWorkBookSheetWithData(workbook, sheet, columnHeadings, data);
    }

    private void addColHeadingsToSheet(Workbook workbook, Sheet sheet, List<Object[]> columnHeadings) {
        Cell cell = null;
        Row row = sheet.createRow(0);
        int colCount = columnHeadings.size();
        for (int i = 0; i < colCount; i++) {
            cell = row.createCell(i);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue((String) columnHeadings.get(i)[0]);
        }
    }

    private void fillWorkBookSheetWithData(
            Workbook workbook,
            Sheet sheet,
            List<Object[]> columnHeadings,
            List<List<Object>> data) {
        Row row = null;
        Cell cell = null;

        int rowIndex = sheet.getLastRowNum() + 1;
        int colIndex = 0;
        CellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("d-mmm-yy"));
        for (List<Object> rowData : data) {
            row = sheet.createRow(rowIndex);
            colIndex = 0;
            for (Object obj : rowData) {
                cell = row.createCell(colIndex);
                updateCellPropertiesByClass(
                        workbook,
                        cell,
                        obj,
                        (Class<?>) columnHeadings.get(colIndex)[1],
                        dateCellStyle);
                colIndex++;
            }
            rowIndex++;
        }
    }

    private void updateCellPropertiesByClass(
            Workbook workbook,
            Cell cell,
            Object obj,
            Class<?> clazz,
            CellStyle dateCellStyle) {
        if (obj == null) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(String.valueOf(obj));
            return;
        }

        if (clazz.equals(Integer.class) || clazz.equals(Double.class)) {
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            if (obj instanceof Integer) {
                cell.setCellValue((Integer) obj);
            }
            else {
                cell.setCellValue((Double) (obj));
            }
            return;
        }
        else if (clazz.equals(String.class)) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(String.valueOf(obj));
            return;
        }
        else if (clazz.equals(Date.class)) {
            cell.setCellValue((Date) obj);
            cell.setCellStyle(dateCellStyle);
            return;
        }
        else {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
            cell.setCellValue(String.valueOf(obj));
            return;
        }
    }
}
