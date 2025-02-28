package sk.adambarca.arbes_task;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

class CsvParser {

    private static final String CSV_DELIMITER = ",";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    List<TelephoneBill> fromCsvString(String csvContent) {
        List<TelephoneBill> bills = new ArrayList<>();
        String[] lines = csvContent.split(System.lineSeparator());

        for (String line : lines) {
            if (!line.isBlank()) {
                bills.add(parseLine(line));
            }
        }

        return bills;
    }

    private TelephoneBill parseLine(String csvLine) {
        String[] parts = csvLine.split(CSV_DELIMITER);

        if (parts.length != 3) {
            throw new InvalidCsvFormat("Invalid CSV format: " + csvLine);
        }

        long phoneNumber = Long.parseLong(parts[0]);
        LocalDateTime startDateTime = LocalDateTime.parse(parts[1], FORMATTER);
        LocalDateTime endDateTime = LocalDateTime.parse(parts[2], FORMATTER);

        return new TelephoneBill(phoneNumber, startDateTime, endDateTime);
    }
}