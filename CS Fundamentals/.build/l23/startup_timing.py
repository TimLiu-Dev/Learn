#!/usr/bin/env python3
"""Median wall-clock startup of `java …` with and without an AOT cache (L23 Part 3).
usage (inside the folder holding app.jar and app.aot): python3 startup_timing.py <MainClass>"""
import statistics, subprocess, sys, time

def median_ms(cmd, runs=20):
    xs = []
    for _ in range(runs):
        t0 = time.perf_counter()
        subprocess.run(cmd, capture_output=True)
        xs.append((time.perf_counter() - t0) * 1000)
    return statistics.median(xs)

main = sys.argv[1]
a = median_ms(["java", "-cp", "app.jar", main])
b = median_ms(["java", "-XX:AOTCache=app.aot", "-cp", "app.jar", main])
print(f"median of 20 runs: without cache {a:.0f} ms, with cache {b:.0f} ms")
