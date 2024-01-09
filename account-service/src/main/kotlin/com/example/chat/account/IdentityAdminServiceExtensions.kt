package com.example.chat.account

import com.example.chat.security.common.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status.Family
import org.keycloak.admin.client.resource.ClientScopeResource
import org.keycloak.admin.client.resource.ProtocolMappersResource
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.representations.idm.ClientRepresentation
import org.keycloak.representations.idm.ClientScopeRepresentation
import org.keycloak.representations.idm.ProtocolMapperRepresentation
import org.keycloak.representations.idm.RealmRepresentation
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException


private const val OPENID_CONNECT = "openid-connect"

internal fun IdentityAdminService.ensureRealmExists(name: String = realmName): RealmResource =
    client
        .also { log.atInfo().log { "Ensuring realm [$name] exists" } }
        .realms()
        .let { realms ->
            realms
                .takeIf { it.findAll().any { realm -> realm.realm == name } }
                ?.also { log.atDebug().log { "Realm [$name] found" } }
                ?.realm(name)
                ?: RealmRepresentation()
                    .apply {
                        realm = name
                    }
                    .let { realms.create(it) }
                    .let { realms.realm(name) }
                    .also { log.atDebug().log { "Realm [${it.toRepresentation().realm}] created" } }
        }
        ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get or create realm")


internal fun RealmResource.ensureClientScopeExists(service: IdentityAdminService): RealmResource =
    existingOrNewClientScope(service)
        .also { service.ensureDefaultClientScopes(this, it) }
        .let { service.ensureClientScopeMappers(it) }
        .let { this }

internal fun IdentityAdminService.ensureDefaultClientScopes(realm: RealmResource, scopeResource: ClientScopeResource) {
    val customScopeName = scopeResource.toRepresentation().name
    val scopes = this.securityProperties.defaultClientScopes.plus(customScopeName)
    scopes
        .also { log.atInfo().log { "Default client scopes: $it" } }
        .filterNot { scopeName -> realm.defaultDefaultClientScopes.any { it.name == scopeName } }
        .mapNotNull { realm.clientScopeByName(it) }
        .also { log.atInfo().log { "Configuring ${it.size} default client scopes" } }
        .map { it.toRepresentation().id }
        .forEach { realm.addDefaultDefaultClientScope(it) }
}

private fun IdentityAdminService.ensureClientScopeMappers(scope: ClientScopeResource) =
    saveClientScopeMappers(scope, securityProperties.tokenAttributes.protocolMappers())

internal fun IdentityAdminService.saveClientScopeMappers(
    scope: ClientScopeResource,
    protocolMappers: List<ProtocolMapperRepresentation>
) =
    scope
        .protocolMappers
        .apply { createNew(this, protocolMappers) }
        .apply { updateExisting(this, protocolMappers) }
        .apply { removeRedundant(this, protocolMappers) }
        .let { log.atInfo().log { "Protocol mappers updated for client scope: ${scope.toRepresentation().name}" } }

private fun IdentityAdminService.createNew(
    protocolMappers: ProtocolMappersResource,
    mappers: List<ProtocolMapperRepresentation>
) =
    mappers
        .filterNot { protocolMappers.mappers.any { mapper -> mapper.name == it.name } }
        .also { log.atDebug().log { "Creating ${it.size} protocol mappers for client scope" } }
        .let { protocolMappers.createMapper(it) }

private fun IdentityAdminService.updateExisting(
    protocolMappers: ProtocolMappersResource,
    mappers: List<ProtocolMapperRepresentation>
) =
    mappers
        .mapNotNull {
            protocolMappers
                .mappers
                .firstOrNull { mapper -> mapper.name == it.name }
                ?.apply {
                    this.config = it.config
                    this.protocolMapper = it.protocolMapper
                    this.protocol = it.protocol
                }
        }
        .also { log.atDebug().log { "Updating ${it.size} protocol mappers for client scope" } }
        .forEach { protocolMappers.update(it.id, it) }

private fun IdentityAdminService.removeRedundant(
    protocolMappers: ProtocolMappersResource,
    mappers: List<ProtocolMapperRepresentation>
) =
    protocolMappers
        .mappers
        .filterNot { mappers.any { mapper -> mapper.name == it.name } }
        .also { log.atDebug().log { "Removing ${it.size} protocol mappers for client scope" } }
        .forEach {
            protocolMappers.delete(it.id)
        }


internal fun RealmResource.ensureAccountClientExists(service: IdentityAdminService) =
    ClientRepresentation()
        .apply {
            clientId = service.securityProperties.clientId
            name = "Account service"
            description = "Client for the account service"
            isPublicClient = true
            protocol = OPENID_CONNECT
            isFullScopeAllowed = true
        }
        .let { service.saveClient(this, it) }

internal fun IdentityAdminService.saveClient(realm: RealmResource, client: ClientRepresentation) =
    realm
        .clients()
        .findByClientId(client.clientId)
        .firstOrNull()
        ?.let { realm.clients().get(it.id) }
        ?.update(client)
        ?.let { log.atInfo().log { "Client ${client.clientId} updated" } }
        ?: realm
            .clients()
            .create(client)
            .let { log.atInfo().log { "Client ${client.clientId} created" } }

private fun RealmResource.existingOrNewClientScope(service: IdentityAdminService): ClientScopeResource =
    existingOrNewClientScope(service, service.realmName)

internal fun RealmResource.existingOrNewClientScope(service: IdentityAdminService, scopeName: String): ClientScopeResource =
    clientScopeByName(scopeName)
        ?: service.createClientScope(this)
private fun IdentityAdminService.createClientScope(realm: RealmResource, scopeName: String = realmName): ClientScopeResource =
    ClientScopeRepresentation()
        .apply {
            name = scopeName
            protocol = OPENID_CONNECT
        }
        .let { realm.clientScopes().create(it) to it }
        .also { (response, scope) ->
            log.atDebug().log { "Client scope [${scope.name}] creation result: ${response.statusInfo.reasonPhrase}" }
        }
        .let { (response, scope) -> createdClientScope(realm, scope, response) }


private fun IdentityAdminService.createdClientScope(
    realm: RealmResource,
    clientScope: ClientScopeRepresentation,
    response: Response
): ClientScopeResource =
    if (listOf(Family.SUCCESSFUL).contains(response.statusInfo.family)) {
        realm.clientScopeByName(clientScope.name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to retrieve client scope")
    } else {
        val message = "Identity service error response: ${response.message()}"
        log.atWarn().log { message }
        throw ResponseStatusException(HttpStatus.BAD_GATEWAY, message)
    }

fun Response.message(): String? =
    if (hasEntity()) jacksonObjectMapper().writeValueAsString(entity) else statusInfo.reasonPhrase

private fun RealmResource.clientScopeByName(clientScopeName: String): ClientScopeResource? =
    clientScopes()
        .let { scope -> scope.findAll().firstOrNull { it.name == clientScopeName } }
        ?.let { clientScopes().get(it.id) }

private fun SecurityProperties.TokenAttributeProperties.protocolMappers(): List<ProtocolMapperRepresentation> =
    listOf(
        middleName.toRepresentation()
    )

private fun TokenMapperProperties.toRepresentation(): ProtocolMapperRepresentation =
    let {
        ProtocolMapperRepresentation()
            .apply {
                name = it.name
                protocol = it.protocol
                protocolMapper = it.mapperName
                config = it.mapperConfig()
            }
    }