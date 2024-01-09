package com.example.chat.message

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MessageServiceApplication

fun main(args: Array<String>) {
    runApplication<MessageServiceApplication>(*args)
}
