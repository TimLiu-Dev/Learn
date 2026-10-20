package ds;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class MyBSTTest {
    private static MyBST<Integer, String> treeOf(int... keys) {
        MyBST<Integer, String> t = new MyBST<>();
        for (int k : keys) t.put(k, "v" + k);
        return t;
    }

    @Test void emptyTreeHasNothingInIt() {
        MyBST<Integer, String> t = new MyBST<>();
        assertEquals(0, t.size());
        assertNull(t.get(1));
        assertFalse(t.contains(1));
        assertEquals(0, t.height());
        assertNull(t.min());
        assertEquals(List.of(), t.inOrderKeys());
    }

    @Test void putThenGet() {
        MyBST<Integer, String> t = treeOf(50, 30, 70);
        assertEquals("v30", t.get(30));
        assertTrue(t.contains(70));
        assertNull(t.get(40));
        assertEquals(3, t.size());
    }

    @Test void putWithAnExistingKeyReplacesTheValue() {
        MyBST<Integer, String> t = treeOf(50, 30);
        t.put(30, "new");
        assertEquals("new", t.get(30));
        assertEquals(2, t.size());
    }

    @Test void inOrderKeysComeOutSorted() {
        assertEquals(List.of(20, 30, 40, 50, 60, 70, 80), treeOf(50, 30, 70, 20, 40, 60, 80).inOrderKeys());
    }

    @Test void minIsTheLeftmostKey() {
        assertEquals(20, treeOf(50, 30, 70, 20, 40).min());
    }

    @Test void heightOfABalancedInsertOrder() {
        assertEquals(3, treeOf(4, 2, 6, 1, 3, 5, 7).height());
    }

    @Test void sortedInsertOrderDegeneratesIntoAList() {
        assertEquals(10, treeOf(IntStream.rangeClosed(1, 10).toArray()).height());
    }

    @Test void deleteALeaf() {
        MyBST<Integer, String> t = treeOf(50, 30, 70, 20);
        t.delete(20);
        assertEquals(List.of(30, 50, 70), t.inOrderKeys());
        assertEquals(3, t.size());
    }

    @Test void deleteANodeWithOneChild() {
        MyBST<Integer, String> t = treeOf(50, 30, 70, 60);
        t.delete(70);
        assertEquals(List.of(30, 50, 60), t.inOrderKeys());
        assertEquals("v60", t.get(60));
    }

    @Test void deleteANodeWithTwoChildrenKeepsItsValuePairs() {
        MyBST<Integer, String> t = treeOf(50, 30, 70, 20, 40, 60, 80, 65);
        t.delete(50);                                    // the root, two children
        assertEquals(List.of(20, 30, 40, 60, 65, 70, 80), t.inOrderKeys());
        assertEquals("v60", t.get(60));
        assertEquals("v65", t.get(65));
        assertNull(t.get(50));
        assertEquals(7, t.size());
    }

    @Test void deletingAMissingKeyChangesNothing() {
        MyBST<Integer, String> t = treeOf(50, 30);
        t.delete(99);
        assertEquals(2, t.size());
    }

    @Test void worksWithAnyComparableKey() {
        MyBST<String, Integer> t = new MyBST<>();
        for (String w : List.of("pear", "apple", "fig")) t.put(w, w.length());
        assertEquals(List.of("apple", "fig", "pear"), t.inOrderKeys());
        assertEquals(5, t.get("apple"));
    }
}
