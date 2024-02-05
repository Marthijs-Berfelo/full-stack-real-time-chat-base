package com.example.chat.security.common

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal

interface OAuth2Principal : OAuth2AuthenticatedPrincipal {
    val userId: String
    val username: String
    val email : String?
    val firstName: String?
    val middleName: String?
    val lastName: String?

    override fun getName(): String = listOfNotNull(firstName, middleName, lastName)
        .joinToString(" ")
        .takeIf(String::isNotBlank)
        ?: username

}

data class DefaultOAuth2Principal(
    override val userId: String,
    override val username: String,
    override val email : String?,
    override val firstName: String?,
    override val middleName: String?,
    override val lastName: String?,
    val nickName: String?,
    val chatUserId: String?,
    private val authorities: List<GrantedAuthority>,
    private val attributes: MutableMap<String, Any>
) : OAuth2Principal {
    override fun getAttributes(): MutableMap<String, Any>  = attributes

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        authorities.toMutableList()
}