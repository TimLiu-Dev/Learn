import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Query {
    public static void main(String[] args) {
        String name = URLEncoder.encode("Ada & Bo", StandardCharsets.UTF_8);
        URI uri = URI.create("http://127.0.0.1:18131/hello?name=" + name);
        System.out.println(name + " | " + uri.getQuery());
    }
}
