# JVM Execution Mental Model Established

Established the two-tier compilation paradigm (source `.java` to architecture-neutral `.class` bytecode via `javac`, executed by the HotSpot JVM with dynamic JIT compilation). Dissected the precise semantics of `public static void main(String[] args)` and introduced bytecode inspection via `javap -c`. Sets the baseline for understanding stack and heap memory in Lesson 0003.
