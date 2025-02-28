package sk.adambarca.arbes_task;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class TelephoneBillCalculatorService implements TelephoneBillCalculator {

    private static final LocalTime PEAK_START = LocalTime.of(8, 0);
    private static final LocalTime PEAK_END = LocalTime.of(16, 0);

    private static final BigDecimal PEAK_RATE = BigDecimal.ONE;

    private final CsvParser csvParser = new CsvParser();


    @Override
    public BigDecimal calculate(String phoneLog) {
        if (phoneLog == null || phoneLog.isBlank()) {
            return BigDecimal.ZERO;
        }

        TelephoneBill telephoneBill = csvParser.fromCsvString(phoneLog);

        return calculateCallCost(telephoneBill);
    }

    private BigDecimal calculateCallCost(TelephoneBill call) {
        LocalDateTime startCount = call.startDateTime();
        long totalMinutes = Duration.between(startCount, call.endDateTime()).toMinutes() + 1;
        BigDecimal totalCost = BigDecimal.ZERO;

        for (long i = 0; i < totalMinutes; i++) {
            LocalTime minuteStart = startCount.toLocalTime();

            if ((minuteStart.equals(PEAK_START) || minuteStart.isAfter(PEAK_START)) && !minuteStart.isBefore(PEAK_END)) {
                totalCost = totalCost.add(PEAK_RATE);
            }

            startCount = call.startDateTime().plusMinutes(1);
        }

        return totalCost;
    }
}
