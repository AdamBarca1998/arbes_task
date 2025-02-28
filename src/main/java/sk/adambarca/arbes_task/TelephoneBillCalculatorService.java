package sk.adambarca.arbes_task;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TelephoneBillCalculatorService implements TelephoneBillCalculator {

    private static final LocalTime PEAK_START = LocalTime.of(8, 0);
    private static final LocalTime PEAK_END = LocalTime.of(16, 0);

    private static final BigDecimal PEAK_RATE = BigDecimal.ONE;
    private static final BigDecimal OFF_PEAK_RATE = BigDecimal.valueOf(0.5);

    private static final BigDecimal SALE_AFTER_5_MINUTES = BigDecimal.valueOf(0.2);

    private final CsvParser csvParser = new CsvParser();


    /**
     * Vypočíta celkovú čiastku na úhradu za telefónne hovory na základe daného výpisu hovorov.
     *
     * <p>Pravidlá výpočtu:</p>
     * <ul>
     *   <li>Hovory prebiehajúce v čase <b>08:00:00 – 16:00:00</b> sú spoplatnené sadzbou <b>1 Kč/min</b>.</li>
     *   <li>Hovory mimo tohto intervalu sú spoplatnené sadzbou <b>0,50 Kč/min</b>.</li>
     *   <li>Každá započatá minúta hovoru sa počíta do ceny podľa času jej začiatku.</li>
     *   <li>Ak hovor trvá dlhšie ako <b>5 minút</b>, každá ďalšia minúta nad tento rámec má sadzbu <b>0,20 Kč/min</b>, bez ohľadu na čas hovoru.</li>
     *   <li>Promo akcia operátora: hovory na <b>najčastejšie volané číslo</b> v rámci výpisu nie sú spoplatnené.</li>
     *   <li>Ak je viacero čísel s rovnakým počtom volaní, vyberie sa číslo s <b>aritmeticky najvyššou hodnotou</b>.</li>
     * </ul>
     *
     * @param phoneLog CSV reťazec obsahujúci záznamy o hovoroch vo formáte:
     *                 {@code <telefonne_cislo>,<start_datum_cas>,<koniec_datum_cas>}
     * @return Celková cena za hovory po aplikovaní všetkých pravidiel a promo akcií.
     */
    @Override
    public BigDecimal calculate(String phoneLog) {
        if (phoneLog == null || phoneLog.isBlank()) {
            return BigDecimal.ZERO;
        }

        List<TelephoneBill> telephoneBills = csvParser.fromCsvString(phoneLog);

        Long mostFrequentNumber = findMostFrequentNumber(telephoneBills).orElse(Long.MIN_VALUE);

        return telephoneBills.stream()
                .filter(call -> call.phoneNumber() != mostFrequentNumber)
                .map(this::calculateCallCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateCallCost(TelephoneBill call) {
        LocalDateTime startCount = call.startDateTime();
        long totalMinutes = Duration.between(startCount, call.endDateTime()).toMinutes() + 1;
        BigDecimal totalCost = BigDecimal.ZERO;

        for (long minuteCount = 0; minuteCount < totalMinutes; minuteCount++) {
            LocalTime minuteStart = startCount.toLocalTime();

            if (minuteCount < 5) {
                if ((minuteStart.equals(PEAK_START) || minuteStart.isAfter(PEAK_START)) && minuteStart.isBefore(PEAK_END)) {
                    totalCost = totalCost.add(PEAK_RATE);
                } else {
                    totalCost = totalCost.add(OFF_PEAK_RATE);
                }
            } else { // after 5 minutes
                totalCost = totalCost.subtract(SALE_AFTER_5_MINUTES);
            }

            startCount = call.startDateTime().plusMinutes(1);
        }

        return totalCost;
    }

    private Optional<Long> findMostFrequentNumber(List<TelephoneBill> telephoneBills) {
        return telephoneBills.stream()
                .collect(Collectors.groupingBy(TelephoneBill::phoneNumber, Collectors.counting()))
                .entrySet().stream()
                .max(Comparator.comparing(Map.Entry<Long, Long>::getValue)
                        .thenComparing(Map.Entry::getKey)
                )
                .map(Map.Entry::getKey);
    }
}
