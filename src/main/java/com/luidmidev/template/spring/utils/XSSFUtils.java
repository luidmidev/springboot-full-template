package com.luidmidev.template.spring.utils;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.function.Consumer;

public final class XSSFUtils {

    private XSSFUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static XSSFRow copyRow(XSSFSheet worksheet, int sourceRowNum, int destinationRowNum) {

        XSSFRow sourceRow = worksheet.getRow(sourceRowNum);
        XSSFRow newRow = worksheet.getRow(destinationRowNum);


        if (newRow != null) {
            worksheet.shiftRows(destinationRowNum, worksheet.getLastRowNum(), 1);
        }
        newRow = worksheet.createRow(destinationRowNum);

        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {

            XSSFCell oldCell = sourceRow.getCell(i);
            XSSFCell newCell = newRow.createCell(i);

            if (oldCell == null) continue;


            XSSFCellStyle newCellStyle = worksheet.getWorkbook().createCellStyle();
            newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
            newCell.setCellStyle(newCellStyle);

            if (oldCell.getCellComment() != null) {
                newCell.setCellComment(oldCell.getCellComment());
            }

            if (oldCell.getHyperlink() != null) {
                newCell.setHyperlink(oldCell.getHyperlink());
            }


            if (oldCell.getCellType() != CellType.FORMULA) {
                newCell.setCellType(oldCell.getCellType());
            }

            switch (oldCell.getCellType()) {
                case BLANK -> newCell.setCellValue(oldCell.getStringCellValue());
                case BOOLEAN -> newCell.setCellValue(oldCell.getBooleanCellValue());
                case FORMULA -> newCell.setCellFormula(oldCell.getCellFormula());
                case NUMERIC -> newCell.setCellValue(oldCell.getNumericCellValue());
                case STRING -> newCell.setCellValue(oldCell.getRichStringCellValue());
            }
        }

        for (int i = 0; i < worksheet.getNumMergedRegions(); i++) {
            CellRangeAddress cellRangeAddress = worksheet.getMergedRegion(i);
            if (cellRangeAddress.getFirstRow() == sourceRow.getRowNum()) {
                CellRangeAddress newCellRangeAddress = new CellRangeAddress(newRow.getRowNum(), (newRow.getRowNum() + (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow())), cellRangeAddress.getFirstColumn(), cellRangeAddress.getLastColumn());
                worksheet.addMergedRegion(newCellRangeAddress);
            }
        }
        return newRow;
    }

    public static void copyRow(XSSFSheet worksheet, int sourceRowNum, int destinationRowNum, Consumer<XSSFRow> onEndCopyRow) {
        onEndCopyRow.accept(copyRow(worksheet, sourceRowNum, destinationRowNum));
    }

    public static void addBorderBoldBottom(XSSFCell cell) {
        XSSFCellStyle style = cell.getCellStyle();
        style.setBorderBottom(BorderStyle.MEDIUM);
        cell.setCellStyle(style);
    }


}
