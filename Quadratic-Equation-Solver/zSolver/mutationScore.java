import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;

public class mutationScore {

    @Test
    public void testValidateInputThrowsException() {
        String input = "1.2000000000000001E10";
        assertThrows(NotEnoughPrecisionException.class, () -> {
            Quadratic.validateInput(input);
        });
    }

    @Test
    public void testSolveQuadraticPrintsSingleRealRoot() throws Exception {
        BigDecimal a = BigDecimal.ONE;
        BigDecimal b = BigDecimal.valueOf(2);
        BigDecimal c = BigDecimal.ONE;

        QuadraticTester.TestResult result = QuadraticTester.runQuadraticSolver(a, b, c);

        assertEquals("-1", result.x1);
        assertNull(result.x2);
    }

    @Test
    public void testSolveQuadraticPrintsTwoRealRoots() throws Exception {
        BigDecimal a = BigDecimal.ONE;
        BigDecimal b = BigDecimal.ZERO;
        BigDecimal c = BigDecimal.valueOf(-1);

        QuadraticTester.TestResult result = QuadraticTester.runQuadraticSolver(a, b, c);

        assertEquals("1", result.x1);
        assertEquals("-1", result.x2);
    }

    @Test
    public void testDetectMutatedQDivA() throws Exception {
        BigDecimal a = BigDecimal.valueOf(2);
        BigDecimal b = BigDecimal.valueOf(8);
        BigDecimal c = BigDecimal.valueOf(6);

        QuadraticTester.TestResult result = QuadraticTester.runQuadraticSolver(a, b, c);

        assertEquals("-3", result.x1);
        assertEquals("-1", result.x2);
    }

    @Test
    public void testSolveQuadraticPrintsPureImaginaryRoots() throws Exception {
        BigDecimal a = BigDecimal.valueOf(2);
        BigDecimal b = BigDecimal.ZERO;
        BigDecimal c = BigDecimal.valueOf(8);

        QuadraticTester.TestResult result = QuadraticTester.runQuadraticSolver(a, b, c);

        assertEquals("2i", result.x1);
        assertEquals("-2i", result.x2);
    }

    @Test
    public void testComplexRootsWithNonUnitAandNonZeroB() throws Exception {
        BigDecimal a = BigDecimal.valueOf(2);
        BigDecimal b = BigDecimal.valueOf(4);
        BigDecimal c = BigDecimal.valueOf(5);

        QuadraticTester.TestResult result = QuadraticTester.runQuadraticSolver(a, b, c);

        assertTrue(
            QuadraticTester.complexEquals(result.x1, "-1 + 1.22474487139i", new BigDecimal("1e-10")),
            "Expected x1 ≈ -1 + 1.2247i, got: " + result.x1
        );
        assertTrue(
            QuadraticTester.complexEquals(result.x2, "-1 - 1.22474487139i", new BigDecimal("1e-10")),
            "Expected x2 ≈ -1 - 1.2247i, got: " + result.x2
        );
    }
}
