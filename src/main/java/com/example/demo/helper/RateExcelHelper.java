package com.example.demo.helper;

import com.example.demo.entity.Rate;
import com.example.demo.service.RateService;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Component
public class RateExcelHelper {

    @Autowired
    private RateService rateService;


    public static final String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String SHEET = "Rates";

    /** Checks if the uploaded file has a valid Excel MIME type. */
    public boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    /**
     * Reads an Excel file and imports {@link Rate} entries.
     *
     * @param is InputStream of uploaded Excel file
     * @return List of successfully saved {@link Rate} objects
     */
    public List<Rate> importRatesFromExcel(InputStream is) {
        List<Rate> savedRates = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheet(SHEET);
            if (sheet == null) throw new IllegalStateException("No sheet named 'Rates' found");

            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter dateTimeFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // skip header

                Rate rate = new Rate();

                rate.setStayDateFrom(getLocalDate(row.getCell(1), dateFmt));
                rate.setStayDateTo(getLocalDate(row.getCell(2), dateFmt));
                rate.setNights(getInt(row.getCell(3)));
                rate.setValue((long) getDouble(row.getCell(4)));
                rate.setBungalowId(getLong(row.getCell(5)));
                rate.setBookDateFrom(getLocalDateTime(row.getCell(6), dateTimeFmt));
                rate.setBookDateTo(getLocalDateTime(row.getCell(7), dateTimeFmt));

                Rate saved = rateService.createRate(rate);
                savedRates.add(saved);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to import Excel file: " + e.getMessage(), e);
        }

        return savedRates;
    }

    // ---------- Helper Methods ----------

    /** Safely parse a LocalDate from a cell (supports string or date formats). */
    private static LocalDate getLocalDate(Cell cell, DateTimeFormatter fmt) {
        if (cell == null) return null;
        try {
            return switch (cell.getCellType()) {
                case STRING -> {
                    String val = cell.getStringCellValue().trim();
                    yield val.isEmpty() ? null : LocalDate.parse(val, fmt);
                }
                case NUMERIC -> cell.getLocalDateTimeCellValue().toLocalDate();
                default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    /** Safely parse a LocalDateTime from a cell (supports string or date formats). */
    private static LocalDateTime getLocalDateTime(Cell cell, DateTimeFormatter fmt) {
        if (cell == null) return null;
        try {
            return switch (cell.getCellType()) {
                case STRING -> {
                    String val = cell.getStringCellValue().trim();
                    yield val.isEmpty() ? null : LocalDateTime.parse(val, fmt);
                }
                case NUMERIC -> cell.getLocalDateTimeCellValue();
                default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    /** Safely parse a numeric double value (handles numeric and string cells). */
    private static double getDouble(Cell cell) {
        if (cell == null) return 0;
        try {
            return switch (cell.getCellType()) {
                case NUMERIC -> cell.getNumericCellValue();
                case STRING -> {
                    String val = cell.getStringCellValue().trim();
                    yield val.isEmpty() ? 0 : Double.parseDouble(val);
                }
                case FORMULA -> cell.getNumericCellValue();
                default -> 0;
            };
        } catch (Exception e) {
            return 0;
        }
    }

    private static long getLong(Cell cell) {
        return (long) getDouble(cell);
    }

    private static int getInt(Cell cell) {
        return (int) getDouble(cell);
    }
}
