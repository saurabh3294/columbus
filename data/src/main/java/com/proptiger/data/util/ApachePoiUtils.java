package com.proptiger.data.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import com.proptiger.exception.ProAPIException;

@Component
public class ApachePoiUtils {

    /**
     * 
     * Generates MS-Excel sheet from a given data. Ensures type consistency
     * across a column.
     * 
     * @param workBookName
     *            : If a workbook exists with this name, a sheet will be added
     *            to it; otherwise a new workbook will be created.
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
    public File exportToMsExcelSheet(
            String workBookName,
            String sheetName,
            List<Object[]> columnHeadings,
            List<List<Object>> data) throws ProAPIException {

        File file = new File(workBookName);
        FileOutputStream out = null;

        /* Create a workbook if none exists */

        if (!file.exists()) {
            try {
                file.createNewFile();
                out = new FileOutputStream(file);
            }
            catch (IOException ex) {
                throw new ProAPIException("Unable to create workbook.", ex);
            }
        }

        Workbook workbook = getOrCreateHSSFWorkBook(workBookName);

        Sheet sheet = getOrCreateSheet(workbook, sheetName);
        
        fillDataInSheet(sheet, columnHeadings, data);

        try {
            workbook.write(out);
            out.close();
        }
        catch (IOException ex) {
            throw new ProAPIException("Unable to write out workbook.", ex);
        }
                
        return file;
    }

    private void fillDataInSheet(Sheet sheet, List<Object[]> columnHeadings, List<List<Object>> data) {

        if (columnHeadings == null || columnHeadings.isEmpty()) {
            throw new ProAPIException("No column headings specified while creating sheet");
        }

        if (data == null) {
            throw new ProAPIException("Null data given while creating sheet");
        }

        Row row = null;
        Cell cell = null;

        /* Fill first row : Heading */

        row = sheet.createRow(0);
        int colCount = columnHeadings.size();
        for (int i = 0; i < colCount; i++) {
            cell = row.createCell(i);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue((String) columnHeadings.get(i)[0]);
        }

        /* Fill rest of the data */
        int rowIndex = 1;
        int colIndex = 0;
        for (List<Object> rowData : data) {
            row = sheet.createRow(rowIndex);
            colIndex = 0;
            for (Object obj : rowData) {
                cell = row.createCell(colIndex);
                cell.setCellType(getCellStyleFromClass((Class<?>) columnHeadings.get(colIndex)[1]));
                cell.setCellValue(String.valueOf(obj));
            }
        }
    }

    private int getCellStyleFromClass(Class<?> clazz) {
        if (clazz.equals(Integer.class) || clazz.equals(Float.class) || clazz.equals(Double.class)) {
            return Cell.CELL_TYPE_NUMERIC;
        }
        else if (clazz.equals(String.class)) {
            return Cell.CELL_TYPE_STRING;
        }
        else {
            return Cell.CELL_TYPE_BLANK;
        }
    }

    private Workbook getOrCreateHSSFWorkBook(String name) {
        return (new HSSFWorkbook());
    }

    private Sheet getOrCreateSheet(Workbook workbook, String sheetName) {
        return workbook.createSheet(sheetName);
    }
}
