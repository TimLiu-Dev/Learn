package runner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class PlanTest {
    private static List<String> ids(String text) throws InputError {
        return Plan.order(JobsFile.parse(text)).stream().map(Job::id).toList();
    }

    @Test
    void startsWithTheJobsThatNeedNothingInFileOrder() throws InputError {
        assertEquals(List.of("a", "c", "b"), ids("id=a duration=1\nid=b duration=1 deps=a\nid=c duration=1"));
    }

    @Test
    void movesDependenciesBeforeTheirDependents() throws InputError {
        assertEquals(List.of("fetch", "build", "report"),
            ids("id=report duration=1 deps=build\nid=build duration=1 deps=fetch\nid=fetch duration=1"));
    }

    @Test
    void reportsACycleAsAPathThatClosesOnItself() {
        assertEquals("dependency cycle: a -> c -> b -> a", assertThrows(InputError.class,
            () -> ids("id=a duration=1 deps=c\nid=b duration=1 deps=a\nid=c duration=1 deps=b\nid=d duration=1")).getMessage());
        assertEquals("dependency cycle: a -> a", assertThrows(InputError.class, () -> ids("id=a duration=1 deps=a")).getMessage());
    }

    @Test
    void backoffDoublesFrom50Ms() {
        assertEquals(List.of(50L, 100L, 200L, 400L), List.of(1, 2, 3, 4).stream().map(Job::backoffMs).toList());
    }
}
