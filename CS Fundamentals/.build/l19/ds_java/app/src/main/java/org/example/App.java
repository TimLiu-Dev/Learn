package org.example;

public class App {
    public static void main(String[] args) {
        String text = """
                The cat sat on the mat. The mat was flat,
                and the cat was fat! A cat, a mat, a hat.""";
        System.out.println(WordCount.report(text));

        LruCache<String, String> cache = new LruCache<>(2);
        cache.put("home", "<html>home</html>");
        cache.put("about", "<html>about</html>");
        cache.get("home");                              // home is now the most recent
        cache.put("blog", "<html>blog</html>");         // evicts about
        System.out.println("cache keys: " + cache.keys() + ", about cached? " + (cache.get("about") != null));
    }
}
