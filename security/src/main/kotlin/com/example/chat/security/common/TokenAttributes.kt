package com.example.chat.security.common

interface TokenAttributes {
    companion object {
        const val USER_NAME = "preferred_username"
        const val FIRST_NAME = "given_name"
        const val LAST_NAME = "family_name"
        const val MIDDLE_NAME = "middle_name"
        const val EMAIL = "email"
        const val FULL_NAME = "name"
        const val NICK_NAME = "nick_name"
    }

    var userName: TokenMapperProperties
    var email: TokenMapperProperties
    var firstName: TokenMapperProperties
    var lastName: TokenMapperProperties
    var middleName: TokenMapperProperties
    var fullName: TokenMapperProperties
    var nickName: TokenMapperProperties
}
