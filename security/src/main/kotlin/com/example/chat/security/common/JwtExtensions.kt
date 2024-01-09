package com.example.chat.security.common

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt


fun Jwt.extractRoles(): List<GrantedAuthority> =
    extractRealmRoles()
        .plus(extractResourceRoles())
        .toAuthorities()

fun Jwt.claim(name: String): String? =
    claims[name] as? String

fun <T> Jwt.claim(name: String, clazz: Class<T>): T =
    claims.extractClaim(name, clazz)

private fun <T> MutableMap<String, Any>.extractClaim(key: String, clazz: Class<T>): T =
    getOrDefault(key, emptyMap<String, Any>())
        .let { jacksonObjectMapper().writeValueAsString(it) }
        .let { jacksonObjectMapper().readValue(it, clazz) }

private fun Jwt.extractRealmRoles(): Set<String> =
    claim(JWT_REALM_ACCESS_CLAIM, mutableMapOf<String, List<String>>()::class.java)
        .extractRoles()

private fun Jwt.extractResourceRoles(): Set<String> =
    claim(JWT_RESOURCE_ACCESS_CLAIM, mutableMapOf<String, MutableMap<String, List<String>>>()::class.java)
        .values
        .flatMap { it.extractRoles() }
        .toSet()

private fun MutableMap<String, List<String>>.extractRoles(): Set<String> =
    getOrDefault(JWT_ROLES, mutableListOf())
        .toSet()

private fun String.toRoleAuthority() =
    if(this.startsWith(DEFAULT_ROLE_PREFIX)) this
    else "$DEFAULT_ROLE_PREFIX$this"

@Suppress("UNCHECKED_CAST")
fun List<GrantedAuthority>.cast(): MutableCollection<GrantedAuthority> =
    this as MutableCollection<GrantedAuthority>

private fun Set<String>.toAuthorities(): List<SimpleGrantedAuthority> =
    map { SimpleGrantedAuthority(it.toRoleAuthority()) }

