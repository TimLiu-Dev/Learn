import java.lang.reflect.Proxy;

public class SelfCall {
    interface Desk {
        void lendBoth();
        void lend();
    }

    static class RealDesk implements Desk {
        public void lendBoth() { lend(); lend(); }
        public void lend() { }
    }

    public static void main(String[] args) {
        int[] seen = {0};
        Desk real = new RealDesk();
        Desk desk = (Desk) Proxy.newProxyInstance(Desk.class.getClassLoader(), new Class<?>[] {Desk.class},
            (proxy, method, params) -> {
                seen[0]++;
                return method.invoke(real, params);
            });
        desk.lendBoth();
        desk.lend();
        System.out.println(seen[0]);
    }
}
