import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DistanceMultipleTest {

    private static List<Double> extractDistances(String output) {
        Pattern p = Pattern.compile("(\\d+\\.\\d+)");
        Matcher m = p.matcher(output);
        List<Double> distances = new ArrayList<>();
        while (m.find()) {
            distances.add(Double.parseDouble(m.group(1)));
        }
        return distances;
    }

    @Test
    public void testSampleRunParsesAndMatchesExpectedValues() {
        String input = String.join("\n",
                "3",
                "(2,3)(7,3)",
                "(4,5)(8,8)", 
                "(-1,-2)(0,7)",
                "");

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintStream origOut = System.out;
        java.io.InputStream origIn = System.in;

        System.setIn(in);
        System.setOut(new PrintStream(outBytes));
        try {
            DistanceMultiple.main(new String[]{});
        } finally {
            System.setOut(origOut);
            System.setIn(origIn);
        }

        String output = outBytes.toString(StandardCharsets.UTF_8);

        List<Double> distances = extractDistances(output);

        if (distances.size() == 0) {
            fail("❌ No distance values found! Make sure your program outputs decimal numbers.\n" +
                 "Example: '5.0' or '9.055'\n" +
                 "Your output was:\n" + output);
        }

        if (distances.size() != 3) {
            fail("❌ Expected 3 distance values, but found " + distances.size() + ".\n" +
                 "Your program should process 3 pairs of points and output 3 distance values.\n" +
                 "Make sure you're reading all 3 input pairs: '(2,3)(7,3)', '(4,5)(8,8)', '(-1,-2)(0,7)'\n" +
                 "Your output was:\n" + output);
        }

        double eps = 1e-10; 
        
        if (Math.abs(distances.get(0) - 5.0) > eps) {
            fail("❌ Wrong distance for first pair (2,3) to (7,3)!\n" +
                 "Expected: 5.0, but got: " + distances.get(0) + "\n" +
                 "Check your calculation: √((7-2)² + (3-3)²) = √(25 + 0) = √25 = 5.0");
        }
        
        if (Math.abs(distances.get(1) - 5.0) > eps) {
            fail("❌ Wrong distance for second pair (4,5) to (8,8)!\n" +
                 "Expected: 5.0, but got: " + distances.get(1) + "\n" +
                 "Check your calculation: √((8-4)² + (8-5)²) = √(16 + 9) = √25 = 5.0");
        }
        
        if (Math.abs(distances.get(2) - 9.055385138137417) > eps) {
            fail("❌ Wrong distance for third pair (-1,-2) to (0,7)!\n" +
                 "Expected: 9.055385138137417, but got: " + distances.get(2) + "\n" +
                 "Check your calculation: √((0-(-1))² + (7-(-2))²) = √(1 + 81) = √82 ≈ 9.055385138137417");
        }
    }
}
