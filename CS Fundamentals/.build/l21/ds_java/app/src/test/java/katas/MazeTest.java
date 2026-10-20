package katas;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class MazeTest {
    @Test
    void findsAShortestPathOf17Steps() {
        List<Maze.Cell> path = Maze.solve(App.MAZE);
        assertNotNull(path);
        assertEquals(17, path.size() - 1);
    }

    @Test
    void pathStartsAtSAndEndsAtE() {
        List<Maze.Cell> path = Maze.solve(App.MAZE);
        Maze.Cell first = path.getFirst(), last = path.getLast();
        assertEquals('S', App.MAZE.get(first.r()).charAt(first.c()));
        assertEquals('E', App.MAZE.get(last.r()).charAt(last.c()));
    }

    @Test
    void everyStepMovesToAnAdjacentOpenCell() {
        List<Maze.Cell> path = Maze.solve(App.MAZE);
        for (int i = 1; i < path.size(); i++) {
            Maze.Cell a = path.get(i - 1), b = path.get(i);
            assertEquals(1, Math.abs(a.r() - b.r()) + Math.abs(a.c() - b.c()));
            assertNotEquals('#', App.MAZE.get(b.r()).charAt(b.c()));
        }
    }

    @Test
    void sNextToEIsOneStep() {
        assertEquals(1, Maze.solve(List.of("SE")).size() - 1);
    }

    @Test
    void walledOffEGivesNull() {
        assertNull(Maze.solve(List.of("S#E")));
    }

    @Test
    void mazeWithoutSIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> Maze.solve(List.of("..E")));
    }
}
