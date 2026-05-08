import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/// Run with Valhalla EA build and observe the difference between:
/// ```
/// java Jep401PerformanceDemoShort.java
/// java --enable-preview Jep401PerformanceDemoShort.java
/// ```
/// On my computer, the second runs about 13x faster than the first.
public class Jep401PerformanceDemoShort {
    void main(String... args) {
        int size = 50_000_000;
        if (args.length > 0) size = Integer.parseInt(args[0]);
        Short[] arr = makeArray(size);
        System.out.println("Array of " + arr.length + " Shorts created.");
        for (int i = 1; i <= 10; i++) {
            double t = time(() -> sumLeastSignificantDigit(arr));
            IO.println("Attempt " + i + ": " + t);
        }
    }

    /// Expensive task to be timed
    long sumLeastSignificantDigit(Short[] shorts) {
        long result = 0;
        for (var d : shorts) result += d % 10;
        return result;
    }

    /// Make an array of Short, unpredictably ordered
    Short[] makeArray(int size) {
        // we have to use a shuffled List to get an unpredictably ordered array
        // HashSet.toArray() won't work because for shorts there would be only 32768 entries
        List<Short> list = new ArrayList<>();
        for (int i = 0; i < size; i++) list.add((short) (i % 32000));
        Collections.shuffle(list);
        return list.toArray(new Short[0]);
    }

    /// Run a task and report the elapsed wall-clock time in ms
    double time(Runnable r) {
        var start = Instant.now();
        r.run();
        var end = Instant.now();
        return Duration.between(start, end).toNanos() / 1_000_000.0;
    }
}
