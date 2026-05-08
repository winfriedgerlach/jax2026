import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/// Run with Valhalla EA build and observe the difference between:
/// ```
/// java Jep401PerformanceDemoArrayListInteger.java
/// java --enable-preview Jep401PerformanceDemoArrayListInteger.java
/// ```
/// On my computer, there is no performance gain although `ArrayList<Integer>` uses `Integer[]` internally.
/// This is obviously not optimized yet in the Valhalla EA builds, which is expected, since they state in JEP 401:
/// > It is not a goal to guarantee any particular optimization strategy or memory layout.
/// > This JEP enables many potential optimizations; only some will be implemented initially.
public class Jep401PerformanceDemoArrayListInteger {
    void main(String... args) {
        int size = 50_000_000;
        if (args.length > 0) size = Integer.parseInt(args[0]);
        List<Integer> list = makeList(size);
        System.out.println("List of " + list.size() + " Integers created.");
        for (int i = 1; i <= 10; i++) {
            double t = time(() -> sumLeastSignificantDigit(list));
            IO.println("Attempt " + i + ": " + t);
        }
    }

    /// Expensive task to be timed
    long sumLeastSignificantDigit(List<Integer> ints) {
        long result = 0;
        for (var d : ints) result += d % 10;
        return result;
    }

    /// Make list of Integer, unpredictably ordered
    List<Integer> makeList(int size) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < size; i++) list.add(i);
        Collections.shuffle(list);
        return list;
    }

    /// Run a task and report the elapsed wall-clock time in ms
    double time(Runnable r) {
        var start = Instant.now();
        r.run();
        var end = Instant.now();
        return Duration.between(start, end).toNanos() / 1_000_000.0;
    }
}
