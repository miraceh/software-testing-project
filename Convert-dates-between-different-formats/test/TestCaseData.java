public class TestCaseData {
    public final String inputDate;
    public final String inputFormat;
    public final String targetFormat;
    public final String expectedOutput;
    public final String caseName;

    public TestCaseData(String inputDate, String inputFormat, String targetFormat, String expectedOutput, String caseName) {
        this.inputDate = inputDate;
        this.inputFormat = inputFormat;
        this.targetFormat = targetFormat;
        this.expectedOutput = expectedOutput;
        this.caseName = caseName;
    }

    @Override
    public String toString() {
        return caseName;
    }
}
