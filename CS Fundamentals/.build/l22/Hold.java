public class Hold {
    public static void main(String[] args) throws InterruptedException {
        byte[] big = new byte[200 * 1024 * 1024];          // 200 MB on the heap, all zeros
        System.out.println("pid " + ProcessHandle.current().pid() + " holding "
                + big.length / 1024 / 1024 + " MB; inspect me now");
        Thread.sleep(60_000);
        System.out.println(big[0]);                        // keep 'big' reachable until the end
    }
}
