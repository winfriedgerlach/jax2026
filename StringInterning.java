/// Simple example to show how String literals are interned in Java, and how the String Pool works.
/// Run with JVM option `-XX:+PrintStringTableStatistics` to see the String Pool statistics in the console output.
///
/// ```
/// java -XX:+PrintStringTableStatistics StringInterning.java
/// ```
///
/// Noticed after writing this that Baeldung has almost identical (and slightly more extensive) code:
/// https://www.baeldung.com/java-string-pool
public class StringInterning {

    // run with JVM option -XX:+PrintStringTableStatistics
    public static void main(String[] args) {
        String a = "foobar";
        String b = "foobar";

        System.out.println("\nTwo string *literals* a and b are auto-deduplicated to String Pool");
        System.out.println("a == b ? " + (a == b));
        System.out.println("a.equals(b) ? " + a.equals(b) + "\n");

        String c = new String("foobar");

        System.out.println("\n'new String()' generates new object, no matter what's in the String Pool");
        System.out.println("a == c? " + (a == c));
        System.out.println("a.equals(c) ? " + a.equals(c) + "\n");

        c = c.intern();

        System.out.println("\nBut new object can be replaced by String Pool reference with 'intern()'");
        System.out.println("a == c after interning c? " + (a == c));
        System.out.println("a.equals(c) ? " + a.equals(c) + "\n");

        // flood the string pool
        int numberOfStrings = 1000_000;
        String[] strings = new String[numberOfStrings];
        for (int i = 0; i < numberOfStrings; i++) {
            String s = "foobar" + i;
            s.intern();

            // avoid that string is immediately GCed
            strings[i] = s;
        }
        System.out.println(strings[10000]);
    }
}
