package dev.shelf

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ShelfspringApplication

fun main(args: Array<String>) {
	runApplication<ShelfspringApplication>(*args)
}
