package com.example.chat.security.common

import com.example.chat.security.common.TokenAttributes.Companion.EMAIL
import com.example.chat.security.common.TokenAttributes.Companion.FIRST_NAME
import com.example.chat.security.common.TokenAttributes.Companion.FULL_NAME
import com.example.chat.security.common.TokenAttributes.Companion.LAST_NAME
import com.example.chat.security.common.TokenAttributes.Companion.MIDDLE_NAME
import com.example.chat.security.common.TokenAttributes.Companion.NICK_NAME
import com.example.chat.security.common.TokenAttributes.Companion.USER_NAME
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "chat.security")
class SecurityProperties {

    lateinit var clientId: String
    var unsecuredResources: List<String> = emptyList()
    var tokenAttributes = TokenAttributeProperties()
    var defaultClientScopes: List<String> = listOf(
        "profile",
        "email",
        "roles"
    )

    open class TokenAttributeProperties : TokenAttributes {
        override var userName = TokenMapperProperties(
            name = "username",
            tokenKey = USER_NAME,
            mapperName = "oidc-usermodel-property-mapper",
            properties = mapOf(
                "claim.name" to USER_NAME,
                "jsonType.label" to "String",
                "user.attribute" to "username"
            )
        )
        override var email = TokenMapperProperties(
            name = EMAIL,
            tokenKey = EMAIL,
            mapperName = "oidc-usermodel-attribute-mapper",
            properties = mapOf(
                "claim.name" to EMAIL,
                "jsonType.label" to "String",
                "user.attribute" to EMAIL
            )
        )
        override var firstName = TokenMapperProperties(
            name = "given name",
            tokenKey = FIRST_NAME,
            mapperName = "oidc-usermodel-attribute-mapper",
            properties = mapOf(
                "claim.name" to FIRST_NAME,
                "jsonType.label" to "String",
                "user.attribute" to "firstName"
            )
        )
        override var lastName = TokenMapperProperties(
            name = "family name",
            tokenKey = LAST_NAME,
            mapperName = "oidc-usermodel-attribute-mapper",
            properties = mapOf(
                "claim.name" to LAST_NAME,
                "jsonType.label" to "String",
                "user.attribute" to "lastName"
            )
        )
        override var middleName = TokenMapperProperties(
            name = "middle name",
            tokenKey = MIDDLE_NAME,
            mapperName = "oidc-usermodel-attribute-mapper",
            properties = mapOf(
                "claim.name" to MIDDLE_NAME,
                "jsonType.label" to "String",
                "user.attribute" to MIDDLE_NAME
            )
        )
        var chatUserId = TokenMapperProperties(
            name = "chat user id",
            tokenKey = "chat_user_id",
            mapperName = "oidc-usermodel-attribute-mapper",
            properties = mapOf(
                "claim.name" to "chat_user_id",
                "jsonType.label" to "String",
                "user.attribute" to "chat_user_id"
            )
        )
        override var fullName = TokenMapperProperties(
            name = "full name",
            tokenKey = FULL_NAME,
            mapperName = "oidc-full-name-mapper"
        )
        override var nickName = TokenMapperProperties(
            name = "nickname",
            tokenKey = NICK_NAME,
            mapperName = "oidc-usermodel-property-mapper",
            properties = mapOf(
                "claim.name" to NICK_NAME,
                "jsonType.label" to "String",
                "user.attribute" to "nickname"
            )
        )
    }
}
