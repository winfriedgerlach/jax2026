import java.util.ArrayList;
import java.util.Arrays;

/// This example shows the differences in memory usage between primitives and their wrapper classes. It can also be used
/// to show the features "compressed OOPs" (default since Java 7, reduces object pointer size from 8 to 4 bytes on 64 bit
/// JVMs), "compact object headers" (final in Java 25, reduces object header from 12 to 8 bytes on 64 bit JVMs),
/// and "value objects" (Valhalla early access release).
///
/// Note that the behavior may vary on different VMs, we assume some variant of Oracle HotSpot VM here.
///
///   - int: 4 bytes --> Integer: 16 bytes (x4)
///   - long: 8 bytes --> Long: 24 bytes (x3)
///   - short: 2 bytes --> Short: 16 bytes (x8)
///
/// As you will most likely store a reference (OOP) to the objects somewhere, this gets even worse:
///
///   - int: 4 bytes --> Integer: 4+16 bytes (x5) or 8+16 bytes (x6)
///   - long: 8 bytes --> Long: 4+24 bytes (x3.375) or 8+24 bytes (x4)
///   - short: 2 bytes --> Short: 4+16 bytes (x10) or 8+16 bytes (x12)
///
/// (since Java 7, OOP size is typically 4 bytes, but will be 8 bytes if max heap size >= 32 GB or compressed OOPs disabled)
///
/// _Note:_ When using the Java 25+ "compact object headers", the Long overhead is reduced to 4+16 bytes (x2.5) or 8+16 bytes (x3)
///
/// ```
/// # run with -Xms5g -Xmx5g to avoid heap resize or GC
/// java -Xms5g -Xmx5g PrimitivesVsWrapperClasses.java
///
/// # run with -XX:-UseCompressedOops or -Xmx32g (or more) to disable compressed object pointers
/// java -XX:-UseCompressedOops -Xms5g -Xmx5g PrimitivesVsWrapperClasses.java
///
/// # run with -XX:-UseCompressedClassPointers to disable compressed class pointers (deprecated in Java 25+)
/// java -XX:-UseCompressedClassPointers -Xms5g -Xmx5g PrimitivesVsWrapperClasses.java
///
/// # run with -XX:+UseCompactObjectHeaders to show "compact object headers" (Java 25+) --> Long now same size as Integer
/// java -XX:+UseCompactObjectHeaders -Xms5g -Xmx5g PrimitivesVsWrapperClasses.java
///
/// # run with Valhalla EA and --enable-preview to show inlining of wrapper classes
/// # --> Array will reserve 8 bytes per inlined Integer, 4 bytes per inlined Short instead of 4 byte for OOP (Long not inlined)
/// # --> Adding Integers/Shorts to the array will not significantly increase memory usage, because the memory is already reserved
/// java --enable-preview -Xms5g -Xmx5g PrimitivesVsWrapperClasses.java
/// ```
public class PrimitivesVsWrapperClasses {

    public static void main(String[] args) throws InterruptedException {
        final int SIZE = 1_000_000;

        // -------------------------------------------------------------------------------------------------------------
        System.out.println("\n--- Create empty arrays of primitive types");

        var baselineMemory = memoryUsage();
        var ints = new int[SIZE];
        var memoryUsageIntArrayAllocation = memoryUsage() - baselineMemory;
        System.out.println("+" + memoryUsageIntArrayAllocation + " memory usage after creating int[] (expected: ~+" + 4 * ints.length + ")");

        baselineMemory = memoryUsage();
        var longs = new long[SIZE];
        var memoryUsageLongArrayAllocation = memoryUsage() - baselineMemory;
        System.out.println("+" + memoryUsageLongArrayAllocation + " memory usage after creating long[] (expected: ~+" + 8 * longs.length + ")");

        baselineMemory = memoryUsage();
        var shorts = new short[SIZE];
        var memoryUsageShortArrayAllocation = memoryUsage() - baselineMemory;
        System.out.println("+" + memoryUsageShortArrayAllocation + " memory usage after creating short[] (expected: ~+" + 2 * shorts.length + ")");

        // -------------------------------------------------------------------------------------------------------------
        System.out.println("\n--- Create arrays of wrapper classes (will contain object pointers [OOPs] only before Valhalla)");

        baselineMemory = memoryUsage();
        var integerObjects = new Integer[SIZE];
        var memoryUsageIntegerArrayAllocation = memoryUsage() - baselineMemory;
        System.out.println("+" + memoryUsageIntegerArrayAllocation + " memory usage after creating Integer[] (expected: ~+" + 4 * integerObjects.length + ")");

        baselineMemory = memoryUsage();
        var longObjects = new Long[SIZE];
        var memoryUsageLongObjectArrayAllocation = memoryUsage() - baselineMemory;
        System.out.println("+" + memoryUsageLongObjectArrayAllocation + " memory usage after creating Long[] (expected: ~+" + 4 * longObjects.length + ")");

        baselineMemory = memoryUsage();
        var shortObjects = new Short[SIZE];
        var memoryUsageShortObjectArrayAllocation = memoryUsage() - baselineMemory;
        System.out.println("+" + memoryUsageShortObjectArrayAllocation + " memory usage after creating Short[] (expected: ~+" + 4 * shortObjects.length + ")");

        // -------------------------------------------------------------------------------------------------------------
        System.out.println("\n--- Fill arrays with primitives");

        baselineMemory = memoryUsage();
        for (int i = 0; i < ints.length; i++) ints[i] = i;
        var memoryUsageFillingIntArray = memoryUsage() - baselineMemory;
        System.out.println("+" + memoryUsageFillingIntArray + " memory usage after filling int[] (expected: no change)");

        baselineMemory = memoryUsage();
        for (int i = 0; i < longs.length; i++) longs[i] = i + 1_000_000L;
        var memoryUsageFillingLongArray = memoryUsage() - baselineMemory;
        System.out.println("+" + memoryUsageFillingLongArray + " memory usage after filling long[] (expected: no change)");

        baselineMemory = memoryUsage();
        for (int i = 0; i < shorts.length; i++) shorts[i] = (short) (i % 32000);
        var memoryUsageFillingShortArray = memoryUsage() - baselineMemory;
        System.out.println("+" + memoryUsageFillingShortArray + " memory usage after filling short[] (expected: no change)");

        // -------------------------------------------------------------------------------------------------------------
        System.out.println("\n--- Fill arrays with wrapper classes");

        baselineMemory = memoryUsage();
        for (int i = 0; i < integerObjects.length; i++) integerObjects[i] = i;
        var memoryUsageFillingIntegerArray = memoryUsage() - baselineMemory;
        System.out.println("+" + memoryUsageFillingIntegerArray + " memory usage after filling Integer[] (expected: ~+" + 16 * integerObjects.length + ")");

        baselineMemory = memoryUsage();
        for (int i = 0; i < longObjects.length; i++) longObjects[i] = (long) (i + 1_000_000);
        var memoryUsageFillingLongObjectArray = memoryUsage() - baselineMemory;
        System.out.println("+" + memoryUsageFillingLongObjectArray + " memory usage after filling Long[] (expected: ~+" + 24 * longObjects.length + ")");

        baselineMemory = memoryUsage();
        for (int i = 0; i < shortObjects.length; i++) shortObjects[i] = (short) (i % 32000);
        var memoryUsageFillingShortObjectArray = memoryUsage() - baselineMemory;
        System.out.println("+" + memoryUsageFillingShortObjectArray + " memory usage after filling Short[] (expected: ~+" + 16 * shortObjects.length + ")");

        // -------------------------------------------------------------------------------------------------------------
        System.out.println("\n--- ArrayList in comparison");

        baselineMemory = memoryUsage();
        var intArrayList = new ArrayList<Integer>(SIZE);
        for (int i = 0; i < SIZE; i++) intArrayList.add(i + 2_000_000);
        var memoryUsageIntegerArrayList = memoryUsage() - baselineMemory;
        System.out.println("+" + memoryUsageIntegerArrayList + " memory usage after creating/filling ArrayList<Integer> (expected: ~+" + (4+16) * intArrayList.size() + ")");

        baselineMemory = memoryUsage();
        var longArrayList = new ArrayList<Long>(SIZE);
        for (int i = 0; i < SIZE; i++) longArrayList.add((long) i + 3_000_000);
        var memoryUsageLongArrayList = memoryUsage() - baselineMemory;
        System.out.println("+" + memoryUsageLongArrayList + " memory usage after creating/filling ArrayList<Long> (expected: ~+" + (4+24) * intArrayList.size() + ")");

        baselineMemory = memoryUsage();
        var shortArrayList = new ArrayList<Short>(SIZE);
        for (int i = 0; i < SIZE; i++) shortArrayList.add((short) (i % 32000));
        var memoryUsageShortArrayList = memoryUsage() - baselineMemory;
        System.out.println("+" + memoryUsageShortArrayList + " memory usage after creating/filling ArrayList<Short> (expected: ~+" + (4+16) * intArrayList.size() + ")");
    }

    static long memoryUsage() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }
}
