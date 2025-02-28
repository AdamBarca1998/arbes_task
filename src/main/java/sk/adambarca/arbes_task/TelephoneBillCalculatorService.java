package sk.adambarca.arbes_task;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class TelephoneBillCalculatorService implements TelephoneBillCalculator {

    private static final LocalTime PEAK_START = LocalTime.of(8, 0);
    private static final LocalTime PEAK_END = LocalTime.of(16, 0);

    private static final BigDecimal PEAK_RATE = BigDecimal.ONE;
    private static final BigDecimal OFF_PEAK_RATE = BigDecimal.valueOf(0.5);

    private static final BigDecimal SALE_AFTER_5_MINUTES = BigDecimal.valueOf(0.2);

    private final CsvParser csvParser = new CsvParser();


    @Override
    public BigDecimal calculate(String phoneLog) {
        if (phoneLog == null || phoneLog.isBlank()) {
            return BigDecimal.ZERO;
        }

        List<TelephoneBill> telephoneBills = csvParser.fromCsvString(phoneLog);

        return telephoneBills.stream()
                .map(this::calculateCallCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateCallCost(TelephoneBill call) {
        LocalDateTime startCount = call.startDateTime();
        long totalMinutes = Duration.between(startCount, call.endDateTime()).toMinutes() + 1;
        BigDecimal totalCost = BigDecimal.ZERO;

        for (long minuteCount = 0; minuteCount < totalMinutes; minuteCount++) {
            LocalTime minuteStart = startCount.toLocalTime();

            if ((minuteStart.equals(PEAK_START) || minuteStart.isAfter(PEAK_START)) && minuteStart.isBefore(PEAK_END)) {
                totalCost = totalCost.add(PEAK_RATE);
            } else {
                totalCost = totalCost.add(OFF_PEAK_RATE);
            }

            if (minuteCount > 4) { // after 5 minutes
                totalCost = totalCost.subtract(SALE_AFTER_5_MINUTES);
            }

            startCount = call.startDateTime().plusMinutes(1);
        }

        return totalCost;
    }
}
