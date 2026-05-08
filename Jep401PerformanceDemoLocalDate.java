import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;

/// Taken from https://inside.java/2025/10/27/try-jep-401-value-classes/
///
/// Run with Valhalla EA build and observe the difference between:
/// ```
/// java Jep401PerformanceDemoLocalDate.java
/// java --enable-preview Jep401PerformanceDemoLocalDate.java
/// ```
/// On my computer, the second runs about 4x faster than the first.
public class Jep401PerformanceDemoLocalDate {
    void main(String... args) {
        int size = 50_000_000;
        if (args.length > 0) size = Integer.parseInt(args[0]);
        LocalDate[] arr = makeArray(size);
        for (int i = 1; i <= 5; i++) {
            double t = time(() -> sumYears(arr));
            IO.println("Attempt " + i + ": " + t);
        }
    }

    /// Expensive task to be timed
    long sumYears(LocalDate[] dates) {
        long result = 0;
        for (var d : dates) result += d.getYear();
        return result;
    }

    /// Make an array of LocalDates, unpredictably ordered
    LocalDate[] makeArray(int size) {
        HashSet<LocalDate> set = new HashSet<>();
        for (int i = 0; i < size; i++) set.add(LocalDate.ofEpochDay(i));
        return set.toArray(new LocalDate[0]);
    }

    /// Run a task and report the elapsed wall-clock time in ms
    double time(Runnable r) {
        var start = Instant.now();
        r.run();
        var end = Instant.now();
        return Duration.between(start, end).toNanos() / 1_000_000.0;
    }
}
