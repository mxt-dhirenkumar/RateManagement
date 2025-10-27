package com.example.demo.helper;

import com.example.demo.entity.Rate;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

/**
 * Exports all Rate records into an Excel file.
 * The generated Excel includes all relevant rate information,
 * including booking period and stay dates.
 */
public class RateExcelExporter {

    public static void export(HttpServletResponse response, List<Rate> rates) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Rates");

            // Header styling
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);

            // Header columns
            String[] headers = {
                    "Rate ID", "Bungalow ID", "Stay Date From", "Stay Date To",
                    "Nights", "Value", "Book Date From", "Book Date To"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Populate rows
            int rowIdx = 1;
            for (Rate rate : rates) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(rate.getRateId() != null ? rate.getRateId() : 0);
                row.createCell(1).setCellValue(rate.getBungalowId());
                row.createCell(2).setCellValue(rate.getStayDateFrom().toString());
                row.createCell(3).setCellValue(rate.getStayDateTo().toString());
                row.createCell(4).setCellValue(rate.getNights());
                row.createCell(5).setCellValue(rate.getValue());
                row.createCell(6).setCellValue(rate.getBookDateFrom() != null ? rate.getBookDateFrom().toString() : "");
                row.createCell(7).setCellValue(rate.getBookDateTo() != null ? rate.getBookDateTo().toString() : "");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write file to HTTP response
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.close();

        } catch (Exception e) {
            throw new RuntimeException("Failed to export rates to Excel: " + e.getMessage(), e);
        }
    }
}
