package sk.adambarca.arbes_task;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TelephoneBillCalculatorImplTest {

    private final TelephoneBillCalculatorImpl telephoneBillCalculator = new TelephoneBillCalculatorImpl();

    @Test
    void calculate_empty() {
        var result = telephoneBillCalculator.calculate("");

        assertThat(result).isZero();
    }
}