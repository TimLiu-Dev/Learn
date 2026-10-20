package demo;

import java.util.List;

record Config(String excitement) {}

/** The "application code": plain methods, wired up only by annotations. */
class Greetings {
    private final Config config;

    public Greetings(Config config) { this.config = config; }

    @Command(name = "greet", help = "greet NAME   say hello")
    public String greet(List<String> args) {
        return "Hello, " + (args.isEmpty() ? "stranger" : args.get(0)) + config.excitement();
    }

    @Command(name = "add", help = "add N...     sum whole numbers")
    public int add(List<String> args) {
        return args.stream().mapToInt(Integer::parseInt).sum();
    }

    @Command(name = "shout", help = "shout WORDS  uppercase them")
    public String shout(List<String> args) {
        return String.join(" ", args).toUpperCase() + config.excitement();
    }

    public String internalHelper() { return "not annotated, so never routed"; }
}
