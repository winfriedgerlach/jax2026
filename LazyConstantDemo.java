import java.util.ArrayList;
import java.util.List;

import static java.lang.IO.println;

/// Run with Java 26 and enable preview features:
/// ```
/// java --enable-preview LazyConstantDemo.java
/// ```
public class LazyConstantDemo {

    // this is certainly not the most clever use of a lazy constant, but it matches the talk and
    // serves to demonstrate the API
    private final LazyConstant<List<String>> lazyList = LazyConstant.of(ArrayList::new);

    void main() {
        LazyConstantDemo demo = new LazyConstantDemo();
        println("Lazy constant created, isInitialized(): " + demo.lazyList.isInitialized());

        println("\nAccessing lazy constant for the first time...");
        var list1 = demo.lazyList.get();
        println("List: " + list1);

        println("\nAccessing lazy constant again...");
        var list2 = demo.lazyList.get();
        println("Same instance returned: " + (list1 == list2));

        println("\nAdding to list...");
        list1.add("Hello");
        println("List after modification: " + list1);
    }
}
