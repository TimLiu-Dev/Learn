# Final Hardware Sync (33 Items / 37 Units) & Pre-Start Curriculum Audit

Completed an end-to-end audit of the finalized 33-item (37-unit) hardware purchase list, the 2027 Master Plan, `index.html`, all 75 HTML lessons, all 8 reference sheets, and all `./labs/` test suites prior to starting Lesson `0000a`.

## Hardware & Reference Synchronization
1. **Added Phase 7 Motor & Rework Gear Across Docs:**
   - Synced `MISSION.md`, `NOTES.md`, `index.html`, `reference/0008-hardware-bringup-buses-cheat-sheet.html`, and Lesson `0038` with the `12GA-N20 6V 300RPM` Hall-encoder motor + `TB6612FNG` driver (`Encoder VCC = 3.3V`, `VM = 2nd MB-102 5V supply`, common `GND`, `STBY = HIGH`) and `HS-02A` soldering/rework consumables (`63/37` solder, `KE-150` flux, brass wool, ESD tweezers, `2016` wick).
   - Updated cart count in `index.html` from 29 to 33 items (37 units).

## Bugs & Alignment Fixes Applied
2. **Fixed Practice Validator Bug in Lesson `0038`:**
   - `runPracticeTest()` previously matched `struct PidState` field names and `pid->integral = 0.0f` in the starter code, passing before the learner wrote any code. Scoped validation to the `pid_step()` body after `float err = setpoint - measured;` and verified `pid->integral +=`, `pid->prev_err = err`, `pid->kp`, and `pid->kd`.
3. **Stitched Canonical 60-Lesson Part A + Part B Navigation Chain:**
   - Rebuilt `<header>` back-links and `<footer>` prev/next links across all 60 core spine lessons (`0000a` -> `0000b` -> `0001`–`0004d` -> `0005`–`0008b` -> `0012a`–`0016a` -> `0024a`–`0026a` -> `0009`–`0012`–`0028f` -> `0017`–`0020g` -> `0021`–`0024e` -> `0033`–`0035` -> `0028e` -> `0037`–`0039` -> `0036`) so sequential study never skips new lessons or detours into Optional/Archived tracks.
4. **Aligned Capstone (`0033`–`0035`) & Interview Playbook (`0036`) to the 2027 Lab:**
   - Replaced stale i.MX8 `/dev/cockpit_bridge` + AAOS `IVehicle` + `SA8295P` + `AOSP 14/15/16` body sections with the actual `nRF54L15-DK` -> `ESP32-S3` -> `Raspberry Pi 4` (`/dev/telemetry_sensor` `INA226`) -> `AOSP 17 Cuttlefish` (`vendor.dev.hardware.fitnesstelemetry`) stack and Weeks 20–21 Mid-Level Gate target.
5. **Filled Concrete Code Gaps:**
   - Added STM32F411 100 MHz PLL clock tree setup (`0006`), Zephyr `BT_GATT_SERVICE_DEFINE` (`0012`), Pi 4 `INA226` `.dtbo` overlay (`0018`), Buildroot-first Pi 4 workflow (`0020b`), and AOSP 17 VTS GTest (`0024e`).
