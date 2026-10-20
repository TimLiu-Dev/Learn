package katas;

public class App {
    public static void main(String[] args) {
        for (String s : new String[] {"{[()]}", "([)]"})
            System.out.println(s + " balanced? " + Brackets.isBalanced(s));
    }
}
