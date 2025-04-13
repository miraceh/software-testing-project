import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.DateFormat;

import org.junit.jupiter.api.Test;

public class z3CoverageScore {
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
    public void testGetDesiredFormat() {
        DateHelper.DateFormats test = DateHelper.DateFormats.D_YYYYMMDD;
        String result = DateHelper.getDesiredFormat(test);
        assertTrue(result != "");
    }

    @Test
    public void testGetDateAndTime() {
        String test = "10000000000";
        String result = DateHelper.getDateAndTime(test);
        assertTrue(result != "");
    }

    @Test
    public void testUnCalledFunc() {
        String test1 = "12/01/2012";
        String test2 = "07/02/2012";
        Long days = DateHelper.getDaysBetweenTwoDate(test1, test2, DateHelper.DateFormats.D_YYMMDD_N);
        assertTrue(days == null);
        days = DateHelper.getDaysBetweenTwoDate(test1, test2, DateHelper.DateFormats.S_DDMMYYYY);
        assertTrue(days != null);

        days = DateHelper.getHoursBetweenTwoDate(test1, test2, DateHelper.DateFormats.D_YYMMDD_N);
        assertTrue(days == null);
        days = DateHelper.getHoursBetweenTwoDate(test1, test2, DateHelper.DateFormats.S_DDMMYYYY);
        assertTrue(days != null);

        days = DateHelper.getMinutesBetweenTwoDates(test1, test2, DateHelper.DateFormats.D_YYMMDD_N);
        assertTrue(days == null);
        days = DateHelper.getMinutesBetweenTwoDates(test1, test2, DateHelper.DateFormats.S_DDMMYYYY);
        assertTrue(days != null);
    
        String res = DateHelper.getDateFromDays(2);
        assertTrue(res != null);
    }

}
