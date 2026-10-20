public class Structures {
    // A linked list is "empty, or a node followed by a smaller list".
    record Node(int value, Node next) {}

    static int length(Node node) {
        return node == null ? 0 : 1 + length(node.next());
    }

    static int sum(Node node) {
        return node == null ? 0 : node.value() + sum(node.next());
    }

    // A tree is "empty, or a node with two smaller trees". Preview of L20.
    record Tree(int value, Tree left, Tree right) {
        Tree(int value) { this(value, null, null); }
    }

    static int size(Tree t) {
        return t == null ? 0 : 1 + size(t.left()) + size(t.right());
    }

    static int height(Tree t) {
        return t == null ? 0 : 1 + Math.max(height(t.left()), height(t.right()));
    }

    public static void main(String[] args) {
        Node list = new Node(3, new Node(1, new Node(4, null)));
        System.out.println("length=" + length(list) + " sum=" + sum(list));

        Tree tree = new Tree(8,
                new Tree(3, new Tree(1), new Tree(6)),
                new Tree(10, null, new Tree(14)));
        System.out.println("size=" + size(tree) + " height=" + height(tree));
    }
}
