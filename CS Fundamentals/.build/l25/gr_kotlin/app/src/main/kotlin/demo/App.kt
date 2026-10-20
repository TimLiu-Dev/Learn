package demo

fun main(args: Array<String>) {
    val container = Container()
    container.register(Config::class, Config(excitement = "!"))
    val router = CommandRouter(container.create(Greetings::class))
    println(router.dispatch(args.toList()))
}
