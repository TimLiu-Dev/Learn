import java.util.List;

// Java code using the KOTLIN model above
public class InteropMain {
    public static void main(String[] args) {
        WorkoutLog log = WorkoutLog.create("Linus");               // @JvmStatic
        log.addStrength("Mon", List.of(new WorkoutSet(ExerciseType.SQUAT, 5, 100.0)));
        log.addCardio("Tue", 25);                                   // @JvmOverloads: km defaults
        log.addRest("Wed");
        System.out.println(log.summary());
        System.out.println("entries: " + log.getAllEntries().size());   // property -> getter
        System.out.println(WorkoutKt.kgToLb(100.0));                // extension -> static method
    }
}
