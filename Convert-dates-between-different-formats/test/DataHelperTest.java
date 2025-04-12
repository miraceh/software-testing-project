import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class DataHelperTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("loadTestCases")
    public void testDateConversion(TestCaseData data) {
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(new ByteArrayOutputStream()));

        long timestamp = 0;
        String actualOutput = "(not computed)";
        String errorStage = "initial";

        try {
            errorStage = "input format parsing";

            if (data.inputFormat.equalsIgnoreCase("auto")) {
                timestamp = DateHelper.parseAnyDate(data.inputDate);
            } else if (data.inputFormat.equalsIgnoreCase("default")) {
                timestamp = DateHelper.getDateOnly(data.inputDate);
            } else {
                DateHelper.DateFormats fmt = parseFormat(data.inputFormat);
                if (fmt == null) {
                    actualOutput = "invalid";
                } else {
                    timestamp = DateHelper.parseDate(data.inputDate, fmt);
                }
            }

            if (timestamp == 0) {
                actualOutput = "invalid";
            } else {
                errorStage = "output format generation";
                if (data.targetFormat.equalsIgnoreCase("default")) {
                    actualOutput = DateHelper.getDateOnly(timestamp);
                } else {
                    DateHelper.DateFormats fmt = parseFormat(data.targetFormat);
                    if (fmt == null) {
                        actualOutput = "invalid";
                    } else {
                        actualOutput = DateHelper.getDesiredFormat(fmt, timestamp);
                    }
                }
            }

            errorStage = "final comparison";
            assertEquals(data.expectedOutput, actualOutput,
                "\n❌ Conversion mismatch\n"
                + "Input Date     : " + data.inputDate + "\n"
                + "Input Format   : " + data.inputFormat + "\n"
                + "Target Format  : " + data.targetFormat + "\n"
                + "Parsed Timestamp: " + timestamp + "\n"
                + "Expected Output: " + data.expectedOutput + "\n"
                + "Actual Output  : " + actualOutput + "\n"
            );

        } catch (AssertionError e) {
            System.err.println("❌ Assertion failed at stage: " + errorStage);
            System.err.println("Case: " + data.caseName);
            System.err.println("Input Date    : " + data.inputDate);
            System.err.println("Input Format  : " + data.inputFormat);
            System.err.println("Target Format : " + data.targetFormat);
            System.err.println("Expected      : " + data.expectedOutput);
            System.err.println("Actual        : " + actualOutput);
            throw e;
        } finally {
            System.setErr(originalErr);
        }
    }

    public static List<TestCaseData> loadTestCases() throws IOException {
        String source = System.getProperty("source", "acts");
        Path folder = Paths.get("genCase", source);

        List<TestCaseData> cases = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.txt")) {
            for (Path path : stream) {
                List<String> lines = Files.readAllLines(path);
                if (lines.size() >= 4) {
                    String caseName = path.getFileName().toString();
                    cases.add(new TestCaseData(
                            lines.get(0).trim(),
                            lines.get(1).trim(),
                            lines.get(2).trim(),
                            lines.get(3).trim(),
                            caseName
                    ));
                }
            }
        }
        return cases;
    }

    private static DateHelper.DateFormats parseFormat(String formatStr) {
        for (DateHelper.DateFormats format : DateHelper.DateFormats.values()) {
            if (format.getDateFormat().equalsIgnoreCase(formatStr)) {
                return format;
            }
        }
        return null;
    }
}
