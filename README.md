# Code zum Vortrag "Wie Java Speicher und Performance verschwendet" auf der JAX 2026

1. [LazyConstantDemo.java](LazyConstantDemo.java): Ganz einfaches Beispiel für die Nutzung von `LazyConstant`s (noch Preview, benötigt Java 26):
2. [ByteArrayOutputStreamDemo.java](ByteArrayOutputStreamDemo.java): Vergleicht die Allokation von Backing-Arrays in `ByteArrayOutputStream` mit der von Spring's `FastByteArrayOutputStream` (minimal modifiziert). Letzterer erhält einmal befüllte Arrays statt sie wegzuschmeißen. Aber Achtung: Dafür werden bei `reset()` anders als beim `ByteArrayOutputStream` die Arrays zurückgesetzt.
3. [CompactStrings.java](CompactStrings.java): Zeigt die Speicherersparnis durch die Nutzung von `Compact Strings` in Java 9+ im Vergleich zu vorherigen Java-Versionen.
4. [StringInterning.java](StringInterning.java): Demonstriert "String-Interning" über den JDK String Pool.
5. [PrimitivesVsWrapperClasses.java](PrimitivesVsWrapperClasses.java): Zeigt die Unterschiede im Speicherverbrauch zwischen primitiven Datentypen und ihren Wrapper-Klassen. Demonstriert auch "compressed OOPs", "compact object headers" und "value objects" (Project Valhalla).
6. Performance-Demos für Valhalla Value Objects:
  - [Jep401PerformanceDemoLocalDate.java](Jep401PerformanceDemoLocalDate.java) - Original-Beispiel aus dem Inside-Java-Blogpost "Try Out JEP 401 Value Classes and Objects".
  - [Jep401PerformanceDemoInteger.java](Jep401PerformanceDemoInteger.java) - angepasst für `Integer[]`
  - [Jep401PerformanceDemoShort.java](Jep401PerformanceDemoShort.java) - angepasst für `Short[]`
  - [Jep401PerformanceDemoLong.java](Jep401PerformanceDemoLong.java) - angepasst für `Long[]` (kein Performancegewinn)
  - [Jep401PerformanceDemoArrayListInteger.java](Jep401PerformanceDemoArrayListInteger.java) - angepasst für `ArrayList<Integer>` (kein Performancegewinn)