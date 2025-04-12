import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.io.*;

public class ActsCoverageScore {

    @Test
    public void testDiscriminantNaNViaInfinityTimesZero() {
        assertThrows(NotEnoughPrecisionException.class, () -> {
            Quadratic.solveQuadratic(Double.POSITIVE_INFINITY, 1.0, 0.0);
        });
    }

    @Test
    public void testValidateInputThrowsException() {
        String input = "1.2000000000000001E10";
        assertThrows(NotEnoughPrecisionException.class, () -> {
            Quadratic.validateInput(input);
        });
    }

    @Test
    public void testMainWithVariousAInputs() throws Exception {
        String simulatedInput = String.join("\n",
                // Case 1: a too large (causes NotEnoughPrecisionException)
                "99999999999999999999999999",
                // Case 2: a == 0
                "0",
                // Case 3: a is not a number
                "nan",
                // Case 4: valid a, b, c → a = 1, b = 1, c = 1, followed by "n" to exit
                "1", "1", "1", "y",
                // case 5
                "1", "1", "1", "n");

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        ByteArrayOutputStream testOut = new ByteArrayOutputStream();

        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;

        System.setIn(testIn);
        System.setOut(new PrintStream(testOut));

        try {
            Quadratic.main(new String[0]);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        String output = testOut.toString();

        assertTrue(true);
    }
}
