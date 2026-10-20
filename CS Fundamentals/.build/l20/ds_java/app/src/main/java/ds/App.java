package ds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {
        String text = "The cat sat on the mat. The mat was flat,\nand the cat was fat! A cat, a mat, a hat.";
        List<String> words = Arrays.stream(text.toLowerCase().split("[^a-z]+"))
            .filter(w -> !w.isEmpty()).toList();

        MyBST<String, Integer> counts = new MyBST<>();
        for (String w : words) {
            Integer old = counts.get(w);
            counts.put(w, old == null ? 1 : old + 1);
        }
        System.out.println(words.size() + " words, " + counts.size() + " unique, tree height " + counts.height());
        System.out.println("sorted: " + counts.inOrderKeys());
        System.out.println("first:  " + counts.min());

        MyMinHeap<WordCount> heap = new MyMinHeap<>();
        for (String w : counts.inOrderKeys()) heap.add(new WordCount(w, counts.get(w)));
        List<WordCount> top3 = new ArrayList<>();
        for (int i = 0; i < 3; i++) top3.add(heap.poll());
        System.out.println("top 3:  " + top3.stream().map(wc -> wc.word() + "=" + wc.count()).collect(Collectors.joining(" ")));

        for (String stop : List.of("a", "the", "on")) counts.delete(stop);   // leaf, two children, one child
        System.out.println("without a/the/on: " + counts.inOrderKeys() + " (height " + counts.height() + ")");
    }
}
