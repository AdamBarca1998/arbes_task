package sk.adambarca.arbes_task;

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

    @Test
    void calculate_zeroMinuteInPeak() throws IOException {
        var csv = getCsv("zero_minute_in_peak");
        var result = telephoneBillCalculator.calculate(csv);

        assertThat(result).isEqualTo(BigDecimal.ONE);
    }



    private String getCsv(String filename) throws IOException {
        var path = Path.of("src","test", "resources", CSV_DIR, filename + CSV);

        return Files.readString(path);
    }
}