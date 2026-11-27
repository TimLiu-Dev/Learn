public class Clicker {
    int count = 0;

    class Button {                       // inner: each Button belongs to one Clicker
        void click() { count++; }        // means Clicker.this.count++
    }

    public static void main(String[] args) {
        Clicker a = new Clicker();
        Clicker b = new Clicker();
        Clicker.Button b1 = a.new Button();
        Clicker.Button b2 = a.new Button();
        Clicker.Button b3 = b.new Button();
        b1.click();
        b2.click();
        b3.click();
        System.out.println(a.count + " " + b.count);
    }
}
