package xyz.ivyxjc.libra.core

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class ApplicationRunner

fun main() {
    SpringApplication.run(ApplicationRunner::class.java)

}