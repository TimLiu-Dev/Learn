import java.util.ArrayList;
import java.util.List;

public class Workout {
    enum ExerciseType {
        SQUAT("Squat"), BENCH("Bench press"), ROW("Row");
        final String label;
        ExerciseType(String label) { this.label = label; }
    }

    record WorkoutSet(ExerciseType exercise, int reps, double weightKg) {}

    sealed interface Entry permits Strength, Cardio, Rest {
        String day();
    }
    record Strength(String day, List<WorkoutSet> sets) implements Entry {}
    record Cardio(String day, int minutes, double km) implements Entry {}
    record Rest(String day) implements Entry {}

    interface Summary {
        String summary();
    }

    // No extensions in Java: static helpers instead
    static double volume(List<WorkoutSet> sets) {
        double total = 0;
        for (WorkoutSet s : sets) total += s.reps() * s.weightKg();
        return total;
    }

    static double kgToLb(double kg) { return kg * 2.20462; }

    static final class WorkoutLog implements Summary {
        private final String owner;
        private final List<Entry> entries = new ArrayList<>();

        private WorkoutLog(String owner) { this.owner = owner; }

        static WorkoutLog create(String owner) { return new WorkoutLog(owner); }   // static factory

        String getOwner() { return owner; }

        List<Entry> getAllEntries() { return List.copyOf(entries); }

        double getTotalVolume() {
            double total = 0;
            for (Entry e : entries) if (e instanceof Strength s) total += volume(s.sets());
            return total;
        }

        void addStrength(String day, List<WorkoutSet> sets) { entries.add(new Strength(day, List.copyOf(sets))); }
        void addCardio(String day, int minutes) { addCardio(day, minutes, 0.0); }   // hand-written overload
        void addCardio(String day, int minutes, double km) { entries.add(new Cardio(day, minutes, km)); }
        void addRest(String day) { entries.add(new Rest(day)); }

        @Override
        public String summary() {
            StringBuilder sb = new StringBuilder("Week for " + owner + "\n");
            for (Entry e : entries) {
                String line = switch (e) {                      // exhaustive over the sealed interface
                    case Strength s -> s.day() + ": strength, " + s.sets().size() + " set(s), "
                            + String.format("%.0f", volume(s.sets())) + " kg";
                    case Cardio c -> c.km() > 0
                            ? c.day() + ": cardio, " + c.minutes() + " min, " + c.km() + " km"
                            : c.day() + ": cardio, " + c.minutes() + " min";
                    case Rest r -> r.day() + ": rest";
                };
                sb.append(line).append('\n');
            }
            double total = getTotalVolume();
            sb.append("Total volume: " + String.format("%.0f", total) + " kg ("
                    + String.format("%.0f", kgToLb(total)) + " lb)");
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        WorkoutLog log = WorkoutLog.create("Ada");
        log.addStrength("Mon", List.of(
                new WorkoutSet(ExerciseType.SQUAT, 5, 100.0),
                new WorkoutSet(ExerciseType.SQUAT, 5, 100.0),
                new WorkoutSet(ExerciseType.BENCH, 8, 60.0)));
        log.addCardio("Tue", 30, 5.2);
        log.addRest("Wed");
        log.addStrength("Thu", List.of(new WorkoutSet(ExerciseType.ROW, 10, 50.0)));
        log.addCardio("Fri", 20);
        System.out.println(log.summary());
        System.out.println("entries: " + log.getAllEntries().size());
    }
}
