import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/// Run with Valhalla EA build and observe the difference between:
/// ```
/// java Jep401PerformanceDemoInteger.java
/// java --enable-preview Jep401PerformanceDemoInteger.java
/// ```
/// On my computer, the second runs about 10x faster than the first.
public class Jep401PerformanceDemoInteger {
    void main(String... args) {
        int size = 50_000_000;
        if (args.length > 0) size = Integer.parseInt(args[0]);
        Integer[] arr = makeArray(size);
        IO.println("Array of " + arr.length + " Integers created.");

        for (int i = 1; i <= 10; i++) {
            double t = time(() -> sumLeastSignificantDigit(arr));
            IO.println("Attempt " + i + ": " + t);
        }
    }

    /// Make an array of Integer, unpredictably ordered
    Integer[] makeArray(int size) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < size; i++) list.add(i);
        Collections.shuffle(list);
        return list.toArray(new Integer[0]);
    }

    /// Expensive task to be timed
    long sumLeastSignificantDigit(Integer[] ints) {
        long result = 0;
        for (var d : ints) result += d % 10;
        return result;
    }

    /// Run a task and report the elapsed wall-clock time in ms
    double time(Runnable r) {
        var start = Instant.now();
        r.run();
        var end = Instant.now();
        return Duration.between(start, end).toNanos() / 1_000_000.0;
    }
}
