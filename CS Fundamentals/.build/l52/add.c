// C, for illustration only: clang compiles it to machine code.
int add(int a, int b) {
    return a + b;
}

int sum(const int *a, int n) {
    int s = 0;
    for (int i = 0; i < n; i++) s += a[i];
    return s;
}
