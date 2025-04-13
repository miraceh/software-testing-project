import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

public class z3MutationScore {

    @Test
    public void testParseDateWithWrongFormat() {
        String badInput = "2024-04-12"; // This is yyyy-MM-dd
        DateHelper.DateFormats expectedFormat = DateHelper.DateFormats.S_DDMMYYYY; // expects dd/MM/yyyy
    
        // Capture System.err output
        PrintStream originalErr = System.err;
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
    
        long result = 0;
        try {
            result = DateHelper.parseDate(badInput, expectedFormat);
        } finally {
            System.setErr(originalErr); // Restore System.err
        }
    
        // Verify the result is 0 (indicating failure)
        assertEquals(0, result, "Expected parseDate to fail and return 0");
    
        // Verify that a stacktrace was printed to System.err
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Exception") || errorOutput.contains("at "),
                "Expected stacktrace in System.err, but none found.\nCaptured:\n" + errorOutput);
    }

    @Test
    public void testGetDateOnlyWithInvalidFormat() {
        String badDate = "2024-04-12"; // wrong format for dd/MM/yyyy

        // Redirect System.err to suppress stack trace (optional)
        PrintStream originalErr = System.err;
        ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errBuffer));

        try {
            long result = DateHelper.getDateOnly(badDate);

            // We expect parsing to fail and result to be 0
            assertEquals(0, result, "Expected getDateOnly to return 0 on invalid format");
        } finally {
            // Restore System.err
            System.setErr(originalErr);
        }

        // Optional: check that ParseException stack trace was printed
        String errorOutput = errBuffer.toString();
        assertTrue(errorOutput.contains("ParseException"), "Expected ParseException to be printed");
    }

    @Test
    public void testParseAnyDateWithUnparseableString() {
        String input = "ABC123!!"; // not a date in any known format

        // Suppress System.err and capture output
        PrintStream originalErr = System.err;
        ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errBuffer));

        long result;
        try {
            result = DateHelper.parseAnyDate(input);
        } finally {
            System.setErr(originalErr); // Restore System.err
        }

        assertEquals(0, result, "Expected parseAnyDate to return 0 on fully unparseable input");

        String errorOutput = errBuffer.toString();
        assertTrue(errorOutput.contains("ParseException") || errorOutput.contains("Unparseable"),
            "Expected parseAnyDate to print parse exception stack traces");
    }

    @Test
    public void testGetDateAndTimeAndTimeOnly() {

        long timestamp = 10000000;

        // Test getDateAndTime(long)
        String formattedLong = DateHelper.getDateAndTime(timestamp);
        assertEquals("31/12/1969, 09:46 PM", formattedLong, "getDateAndTime(long) mismatch");

        // Test getTimeOnly
        String timeOnly = DateHelper.getTimeOnly(timestamp);
        assertEquals("09:46 PM", timeOnly, "getTimeOnly mismatch");
    }

    @Test
    public void testGetTodayTomorrow() {
        String result;
        result = DateHelper.getTodayWithTime();
        assertTrue(result != "");
        result = DateHelper.getToday();
        assertTrue(result != "");
        result = DateHelper.getTomorrow();
        assertNotNull(result, "getTomorrow() returned null");
        assertFalse(result.trim().isEmpty(), "getTomorrow() returned empty string");
        assertTrue(result.matches("\\d{2}/\\d{2}/2025"), "Expected year 2025 but got: " + result);
    }

    @Test
    public void testUnCalledFunc1() {
        String test = "01/01/1970";
        long res = DateHelper.getDateOnly(test);
        assertTrue(res == 18000000);

        long time = 1000;
        String res2 = DateHelper.getDateOnly(time);
        assertTrue(res2.equals("31/12/1969"));

        test = "01/01/1970";
        res = DateHelper.parseAnyDate(test);
        assertTrue(res == 18000000 );

        DateHelper.DateFormats format = DateHelper.DateFormats.D_YYMMDD_N;
        test = DateHelper.getDesiredFormat(format);
        assertTrue(test != "");

        test = DateHelper.getDesiredFormat(format, 1800000);
        assertTrue(test != "");
    }

    @Test
    public void testUnCalledFunc() {
        String test1 = "12/01/2012";
        String test2 = "07/02/2012";
        
        Long days = DateHelper.getDaysBetweenTwoDate(test1, test2, DateHelper.DateFormats.D_YYMMDD_N);
        assertTrue(days == null);
        days = DateHelper.getHoursBetweenTwoDate(test1, test2, DateHelper.DateFormats.D_YYMMDD_N);
        assertTrue(days == null);
        days = DateHelper.getMinutesBetweenTwoDates(test1, test2, DateHelper.DateFormats.D_YYMMDD_N);
        assertTrue(days == null);


        days = DateHelper.getDaysBetweenTwoDate(test1, test2, DateHelper.DateFormats.S_DDMMYYYY);
        assertTrue(days == -26);
        days = DateHelper.getHoursBetweenTwoDate(test1, test2, DateHelper.DateFormats.S_DDMMYYYY);
        assertTrue(days == -624);
        days = DateHelper.getMinutesBetweenTwoDates(test1, test2, DateHelper.DateFormats.S_DDMMYYYY);
        assertTrue(days == -37440);
    
        String res = DateHelper.getDateFromDays(2);
        assertTrue(res != null);
    }

}
