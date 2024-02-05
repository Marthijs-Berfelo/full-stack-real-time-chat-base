package com.example.chat.common

private val passwordChars: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

fun generatePassword(size: Int = 16) =
    List(size) { passwordChars.random() }
        .joinToString("")