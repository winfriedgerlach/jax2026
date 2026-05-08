import static java.lang.IO.println;

/// Run with Java 25+ (uses JEP 512: Compact Source Files and Instance Main Methods, cf. https://openjdk.org/jeps/512):
/// ```
/// java ByteArrayOutputStreamDemo.java
/// ```
void main(String[] args) throws IOException {
    println("Usage: java ByteArrayOutputStreamDemo.java [numberOfBytesToWrite] [initialSize]");
    int numberOfBytesToWrite = 1500;
    if (args.length > 0) {
        numberOfBytesToWrite = Integer.parseInt(args[0]);
    }
    int initialSize = 256;
    if (args.length > 1) {
        initialSize = Integer.parseInt(args[1]);
    }

    println("Writing %d bytes to BOS with initial size %d bytes...%n".formatted(numberOfBytesToWrite, initialSize));

    println("Testing MyBos (extends ByteArrayOutputStream)...");
    MyBos bos = new MyBos(initialSize); // default size would be 32 bytes
    for (int i = 0; i < numberOfBytesToWrite; i++) {
        bos.write(42);
    }
    println("Total bytes copied during resizes: " + bos.numCopies);
    println("Total bytes allocated: " + bos.numAllocatedBytes);

    println("\nTesting MyFastBos (extends FastByteArrayOutputStream)...");
    MyFastBos fastBos = new MyFastBos(initialSize); // default size would be 256 bytes
    for (int i = 0; i < numberOfBytesToWrite; i++) {
        fastBos.write(42);
    }
    println("Total bytes copied during resizes: 0");
    println("Total bytes allocated: " + fastBos.numAllocatedBytes);
}

static class MyBos extends ByteArrayOutputStream {

    long numCopies = 0;
    long numAllocatedBytes = 0;

    public MyBos() {
        super();
        numAllocatedBytes = buf.length;
    }

    public MyBos(int capacity) {
        super(capacity);
        numAllocatedBytes = buf.length;
    }

    @Override
    public synchronized void write(int b) {
        int oldSize = buf.length;
        super.write(b);
        if (oldSize != buf.length) {
            println("Buffer resized: %d -> %d".formatted(oldSize, buf.length));
            numCopies += oldSize; // each resize copies oldSize bytes
            numAllocatedBytes += buf.length;
        }
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) {
        int oldSize = buf.length;
        super.write(b, off, len);
        if (oldSize != buf.length) {
            println("Buffer resized: %d -> %d".formatted(oldSize, buf.length));
            numCopies += oldSize; // each resize copies oldSize bytes
            numAllocatedBytes += buf.length;
        }
    }
}

static class MyFastBos extends FastByteArrayOutputStream {

    long numAllocatedBytes = 0;

    public MyFastBos() {
        super();
    }

    public MyFastBos(int capacity) {
        super(capacity);
    }

    @Override
    public synchronized void write(int b) throws IOException {
        int oldSize = buffers.size();
        super.write(b);
        if (oldSize != buffers.size()) {
            int addedBufferSize = buffers.getLast().length;
            println("Buffers: " + buffers.stream().map(buf -> "" + buf.length).collect(Collectors.joining(",")));
            numAllocatedBytes = buffers.stream().mapToInt(buf -> buf.length).sum();
        }
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        int oldSize = buffers.size();
        super.write(b, off, len);
        if (oldSize != buffers.size()) {
            int addedBufferSize = buffers.getLast().length;
            println("Buffers: " + buffers.stream().map(buf -> "" + buf.length).collect(Collectors.joining(",")));
            numAllocatedBytes = numAllocatedBytes == 0 ? buffers.getFirst().length + addedBufferSize : numAllocatedBytes + addedBufferSize;
        }
    }
}

