import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class QuadraticParamTest {

    static Stream<File> inputFiles() {
        // Read test case source from system property
        String source = System.getProperty("source", "z3");
        File folder = new File("./genCase/" + source);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
        return files != null ? Stream.of(files) : Stream.empty();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("inputFiles")
    public void testEachInputFile(File file) throws Exception {
        var input = QuadraticTester.readInputFromFile(file);
        var result = QuadraticTester.runQuadraticSolver(input.a, input.b, input.c);
        boolean passed = QuadraticTester.compareWithExpected(result, input);

        System.out.printf("[%s] x1=%s, x2=%s → %s%n",
                file.getName(), result.x1, result.x2, passed ? "✅ PASS" : "❌ FAIL");

        assertTrue(passed, "Test failed for file: " + file.getName());
    }
}
