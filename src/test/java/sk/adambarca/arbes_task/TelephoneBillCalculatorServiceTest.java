package sk.adambarca.arbes_task;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TelephoneBillCalculatorServiceTest {

    private static final String CSV_DIR = "csv";
    private static final String CSV = ".csv";

    private final TelephoneBillCalculatorService telephoneBillCalculator = new TelephoneBillCalculatorService();

    @Test
    void calculate_empty() {
        var result = telephoneBillCalculator.calculate("");

        assertThat(result).isZero();
    }

    @Nested
    class PeakRateTests {
        @Test
        void calculate_zeroMinuteInPeak() throws IOException {
            var csv = getCsv("zero_minute_in_peak");
            var result = telephoneBillCalculator.calculate(csv);

            assertThat(result).isEqualTo(BigDecimal.ONE);
        }

        @Test
        void calculate_secondsInPeak() throws IOException {
            var csv = getCsv("zero_minute_in_peak");
            var result = telephoneBillCalculator.calculate(csv);

            assertThat(result).isEqualTo(BigDecimal.ONE);
        }

        @Test
        void calculate_2minutesInPeak() throws IOException {
            var csv = getCsv("2_minutes_in_peak");
            var result = telephoneBillCalculator.calculate(csv);

            assertThat(result).isEqualTo(BigDecimal.TWO);
        }
    }

    @Nested
    class OffPeakRateTests {

        @Test
        void calculate_zeroMinuteInOffPeak() throws IOException {
            var csv = getCsv("zero_minute_in_off_peak");
            var result = telephoneBillCalculator.calculate(csv);

            assertThat(result).isEqualTo(BigDecimal.valueOf(0.5));
        }

        @Test
        void calculate_secondsInOffPeak() throws IOException {
            var csv = getCsv("zero_minute_in_off_peak");
            var result = telephoneBillCalculator.calculate(csv);

            assertThat(result).isEqualTo(BigDecimal.valueOf(0.5));
        }

        @Test
        void calculate_2minutesInOffPeak() throws IOException {
            var csv = getCsv("2_minutes_in_off_peak");
            var result = telephoneBillCalculator.calculate(csv);

            assertThat(result).isEqualTo(BigDecimal.valueOf(1.0));
        }
    }

    @Test
    void calculate_betweenPeak() throws IOException {
        var csv = getCsv("between_peak");
        var result = telephoneBillCalculator.calculate(csv);

        assertThat(result).isEqualTo(BigDecimal.valueOf(1.5));
    }

    @Test
    void task() throws IOException {
        var csv = getCsv("task");
        var result = telephoneBillCalculator.calculate(csv);

        assertThat(result).isEqualTo(BigDecimal.valueOf(12.5));
    }

    private String getCsv(String filename) throws IOException {
        var path = Path.of("src","test", "resources", CSV_DIR, filename + CSV);

        return Files.readString(path);
    }
}