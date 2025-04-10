import java.io.*;
import java.math.BigDecimal;
import java.util.regex.*;

public class QuadraticTester {

    public static class TestCase {
        public BigDecimal a, b, c;
        public String expectedX1;
        public String expectedX2;
    }

    public static class TestResult {
        public String x1;
        public String x2;
        public String rawOutput;
    }

    // === Read test input file (supports real or complex roots in x =) ===
    public static TestCase readInputFromFile(File file) {
        TestCase tc = new TestCase();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            tc.a = new BigDecimal(reader.readLine().trim());
            tc.b = new BigDecimal(reader.readLine().trim());
            tc.c = new BigDecimal(reader.readLine().trim());

            String fourthLine = reader.readLine();
            if (fourthLine != null && fourthLine.trim().startsWith("x =")) {
                String[] parts = fourthLine.trim().substring(3).trim().split(",");
                if (parts.length >= 1) tc.expectedX1 = parts[0].trim();
                if (parts.length >= 2) tc.expectedX2 = parts[1].trim();
            }
        } catch (Exception e) {
            tc.a = BigDecimal.ONE;
            tc.b = new BigDecimal("2");
            tc.c = new BigDecimal("1");
        }

        BigDecimal LIMIT = new BigDecimal("9999999999999998");
        if (tc.a.compareTo(BigDecimal.ZERO) == 0 ||
            tc.a.compareTo(LIMIT) > 0 ||
            tc.b.compareTo(LIMIT) > 0 ||
            tc.c.compareTo(LIMIT) > 0) {
            tc.a = BigDecimal.ONE;
            tc.b = new BigDecimal("2");
            tc.c = new BigDecimal("1");
        }

        return tc;
    }

    // === Run the solver and capture x1/x2 from output ===
    public static TestResult runQuadraticSolver(BigDecimal a, BigDecimal b, BigDecimal c) throws Exception {
        String input = a.toPlainString() + "\n" + b.toPlainString() + "\n" + c.toPlainString() + "\nn\n";
        ByteArrayInputStream fakeIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(fakeIn);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream captureOut = new PrintStream(baos);
        PrintStream originalOut = System.out;
        System.setOut(captureOut);

        Quadratic.main(new String[0]);

        System.out.flush();
        System.setOut(originalOut);

        String output = baos.toString();
        Matcher m1 = Pattern.compile("x1\\s*=\\s*(.+)").matcher(output);
        Matcher m2 = Pattern.compile("x2\\s*=\\s*(.+)").matcher(output);

        TestResult result = new TestResult();
        result.rawOutput = output;
        if (m1.find()) result.x1 = m1.group(1).trim();
        if (m2.find()) result.x2 = m2.group(1).trim();
        return result;
    }

    // === Complex string parser: returns [real, imag] ===
    private static BigDecimal[] parseComplex(String s) {
        s = s.replaceAll("\\s+", "");

        // Normalize things like "4999999 + -1i" → "4999999 - 1i"
        s = s.replaceAll("\\+\\-", "-");
        s = s.replaceAll("\\-\\-", "+");

        // Handle special case: "i" or "-i"
        if (s.equals("i")) {
            return new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ONE };
        } else if (s.equals("-i")) {
            return new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ONE.negate() };
        }

        // Match patterns like "a+bi" or "a-bi"
        Pattern p = Pattern.compile("([+-]?[\\d\\.Ee]+)([+-])([\\d\\.Ee]+)i");
        Matcher m = p.matcher(s);
        if (m.matches()) {
            BigDecimal real = new BigDecimal(m.group(1));
            BigDecimal imag = new BigDecimal(m.group(3));
            if (m.group(2).equals("-")) {
                imag = imag.negate();
            }
            return new BigDecimal[] { real, imag };
        }

        // Otherwise, assume pure real
        return new BigDecimal[] { new BigDecimal(s), BigDecimal.ZERO };
    }
    

    // === Compare two complex strings with epsilon tolerance ===
    private static boolean complexEquals(String a, String b, BigDecimal epsilon) {
        if (a == null || b == null) return false;
        try {
            BigDecimal[] ca = parseComplex(a);
            BigDecimal[] cb = parseComplex(b);

            boolean realMatch = ca[0].subtract(cb[0]).abs().compareTo(epsilon) <= 0;
            boolean imagMatch = ca[1].subtract(cb[1]).abs().compareTo(epsilon) <= 0;

            return realMatch && imagMatch;
        } catch (Exception e) {
            return false;
        }
    }

    // === Compare actual vs expected result (supports complex roots) ===
    public static boolean compareWithExpected(TestResult actual, TestCase expected) {
        BigDecimal EPS = new BigDecimal("1e-12");

        if ((expected.expectedX1 != null && actual.x1 == null) ||
                (expected.expectedX2 != null && expected.expectedX2.length() > 0 && actual.x2 == null)) {

            System.out.println("❌ One of the actual roots is null but expected was not:");
            if (expected.expectedX1 != null && actual.x1 == null) {
                System.out.printf("   Expected x1: %s%n", expected.expectedX1);
                System.out.println("   Actual x1: null");
            }
            if (expected.expectedX2 != null && expected.expectedX2.length() > 0 && actual.x2 == null) {
                System.out.printf("   Expected x2: %s%n", expected.expectedX2);
                System.out.println("   Actual x2: null");
            }
            return false;
        }

        if (expected.expectedX1 != null &&
                !complexEquals(actual.x1, expected.expectedX1, EPS)) {
            System.out.println("❌ x1 mismatch");
            System.out.printf("   Expected x1: %s%n", expected.expectedX1);
            System.out.printf("   Actual   x1: %s%n", actual.x1);
            return false;
        }

        if (expected.expectedX2 != null && expected.expectedX2.length() > 0 &&
                !complexEquals(actual.x2, expected.expectedX2, EPS)) {
            System.out.println("❌ x2 mismatch");
            System.out.printf("   Expected x2: %s%n", expected.expectedX2);
            System.out.printf("   Actual   x2: %s%n", actual.x2);
            return false;
        }

        return true;
    }
}
