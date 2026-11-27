__attribute__((noinline)) int twice(int x) { return x + x; }

int quad(int x) { return twice(twice(x)); }
