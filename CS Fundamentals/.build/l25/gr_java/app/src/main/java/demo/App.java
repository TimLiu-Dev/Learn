package demo;

import java.util.List;

public class App {
    public static void main(String[] args) {
        Container container = new Container();
        container.register(Config.class, new Config("!"));
        CommandRouter router = new CommandRouter(container.create(Greetings.class));
        System.out.println(router.dispatch(List.of(args)));
    }
}
