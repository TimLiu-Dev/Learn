#!/usr/bin/env python3
import data_phase2_part1
import data_phase2
import data_phase2_part2

ALL_PHASE2 = [
    data_phase2_part1.LESSON_0009,
    data_phase2_part1.LESSON_0010,
]
ALL_PHASE2.extend(data_phase2.REMAINING_PHASE2)
ALL_PHASE2.extend(data_phase2_part2.PHASE2_PART2)

print(f"Loaded all {len(ALL_PHASE2)} Phase 2 lessons successfully.")
