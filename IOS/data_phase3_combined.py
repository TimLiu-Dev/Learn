#!/usr/bin/env python3
import data_phase3
import data_phase3_rest
import data_phase3_part2

ALL_PHASE3 = []
ALL_PHASE3.extend(data_phase3.PHASE3_LESSONS)
ALL_PHASE3.extend(data_phase3_rest.REMAINING_PHASE3)
ALL_PHASE3.extend(data_phase3_part2.PHASE3_PART2)

print(f"Loaded all {len(ALL_PHASE3)} Phase 3 lessons successfully.")
