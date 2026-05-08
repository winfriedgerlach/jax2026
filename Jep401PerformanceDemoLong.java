import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/// Run with Valhalla EA build and observe the difference between:
/// ```
/// java Jep401PerformanceDemoLong.java
/// java --enable-preview Jep401PerformanceDemoLong.java
/// ```
/// On my computer, the second runs **slower** than the first!
/// Longs plus their null header bit are larger than 64 bit and thus cannot be written atomically, so they are not inlined.
/// Future null-restricted Longs can probably be inlined, but they are out-of-scope for JEP 401.
public class Jep401PerformanceDemoLong {
    void main(String... args) {
        int size = 50_000_000;
        if (args.length > 0) size = Integer.parseInt(args[0]);
        Long[] arr = makeArray(size);
        System.out.println("Array of " + arr.length + " Longs created.");
        for (int i = 1; i <= 10; i++) {
            double t = time(() -> sumLeastSignificantDigit(arr));
            IO.println("Attempt " + i + ": " + t);
        }
    }

    /// Expensive task to be timed
    long sumLeastSignificantDigit(Long[] longs) {
        long result = 0;
        for (var d : longs) result += d % 10;
        return result;
    }

    /// Make an array of Long, unpredictably ordered
    Long[] makeArray(int size) {
        List<Long> list = new ArrayList<>();
        for (int i = 0; i < size; i++) list.add((long) i);
        Collections.shuffle(list);
        return list.toArray(new Long[0]);
    }

    /// Run a task and report the elapsed wall-clock time in ms
    double time(Runnable r) {
        var start = Instant.now();
        r.run();
        var end = Instant.now();
        return Duration.between(start, end).toNanos() / 1_000_000.0;
    }
}
