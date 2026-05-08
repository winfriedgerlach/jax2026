

/// Shows that Java strings can be stored in a compact form (1 byte per character) if they only contain characters from
/// the Latin-1 character set (ISO 8859-1). See JEP 254: Compact Strings (https://openjdk.org/jeps/254, since Java 9).
///
/// JVM option to disable compact strings: `-XX:-CompactStrings`
/// --> slight performance gain if application uses predominantly multi-byte characters (Chinese, Japanese,...),
/// because not every string needs to be checked for non-Latin-1 characters upon creation.
///
/// Run with and without `-XX:-CompactStrings`:
/// ```
/// java CompactStrings.java
/// java -XX:-CompactStrings CompactStrings.java
/// ```
public class CompactStrings {

    public static void main(String[] args) throws InterruptedException {

        var baselineMemory = memoryUsage();
        String aaa = "a".repeat(1_000_000);
        var memoryUsage1stLatin1String = memoryUsage() - baselineMemory;
        System.out.println("+" + memoryUsage1stLatin1String + " memory usage after creating 1st Latin1 string (expected: ~+" + aaa.length() +")");

        baselineMemory = memoryUsage();
        String aaaö = aaa + "ö"; // ö is in Latin-1 (ISO 8859-1)
        var memoryUsage2ndLatin1String = memoryUsage() - baselineMemory;
        System.out.println("+" + memoryUsage2ndLatin1String + " memory usage after creating 2nd Latin1 string (expected: ~+" + aaaö.length() + ")");

        baselineMemory = memoryUsage();
        String aaaSmile = aaa + "😭";
        var memoryUsageUtf16String = memoryUsage() - baselineMemory;
        System.out.println("+" + memoryUsageUtf16String + " memory usage after string with Emoji (--> enforces UTF-16) (expected: ~+" + (aaaSmile.length() * 2) + ")");

        baselineMemory = memoryUsage();
        String bbb = "b".repeat(1_000_000);
        var memoryUsageYetAnotherLatin1String = memoryUsage() - baselineMemory;
        System.out.println("+" + memoryUsageYetAnotherLatin1String + " memory usage after creating another Latin1 string (expected: ~+" + bbb.length() + ")");

        baselineMemory = memoryUsage();
        String japaneseBbb = "横" + bbb;
        var memoryUsageYetAnotherUTF16String = memoryUsage() - baselineMemory;
        System.out.println("+" + memoryUsageYetAnotherUTF16String + " memory usage after string with Japanese character (--> enforces UTF-16) (expected: ~+" + (japaneseBbb.length() * 2) + ")");
    }

    static long memoryUsage() throws InterruptedException {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }
}
