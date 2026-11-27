package runner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class JobsFileTest {
    @Test
    void parsesEveryFieldAndFillsInTheDefaults() throws InputError {
        List<Job> jobs = JobsFile.parse("id=a duration=200 fail=2 deps=b,c\nid=b duration=10\nid=c duration=0 deps=");
        assertEquals(new Job("a", 200, 2, List.of("b", "c")), jobs.get(0));
        assertEquals(new Job("b", 10, 0, List.of()), jobs.get(1));
        assertEquals(new Job("c", 0, 0, List.of()), jobs.get(2));
    }

    @Test
    void skipsCommentsAndBlankLinesButCountsThemInLineNumbers() {
        var e = assertThrows(InputError.class, () -> JobsFile.parse("# header\n\nid=a duration=1\nid=b duration=x"));
        assertEquals("line 4: duration must be a whole number ≥ 0, got 'x'", e.getMessage());
    }

    @Test
    void namesTheLineAndTheProblem() {
        assertEquals("line 2: unknown key 'colour'",
            assertThrows(InputError.class, () -> JobsFile.parse("id=a duration=1\nid=b duration=1 colour=red")).getMessage());
        assertEquals("line 2: duplicate id 'a' (first on line 1)",
            assertThrows(InputError.class, () -> JobsFile.parse("id=a duration=1\nid=a duration=2")).getMessage());
        assertEquals("line 1: unknown dependency 'ghost' in job 'a'",
            assertThrows(InputError.class, () -> JobsFile.parse("id=a duration=1 deps=ghost")).getMessage());
    }
}
