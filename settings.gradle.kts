rootProject.name = "full-stack-real-time-chat"

include(
    "contract",
    "security",
    "account-service",
    "message-service",
    "user-service"
)

dependencyResolutionManagement {
    versionCatalogs {
        create("lib") {
            version("java", JavaVersion.VERSION_17.name)
        }
        create("kt") {
            val kotlinVersion: String by settings
            plugin("jvm", "org.jetbrains.kotlin.jvm").version(kotlinVersion)
            plugin("spring", "org.jetbrains.kotlin.plugin.spring").version(kotlinVersion)
        }
        create("spring") {
            val springBootVersion: String by settings
            val springDependenciesVersion: String by settings
            val graalVmVersion: String by settings
            val springCloudVersion: String by settings
            plugin("boot", "org.springframework.boot").version(springBootVersion)
            plugin("dependencyManagement", "io.spring.dependency-management").version(springDependenciesVersion)
            plugin("nativeBuild", "org.graalvm.buildtools.native").version(graalVmVersion)
            library("bootParent", "org.springframework.boot", "spring-boot-starter-parent").version(springBootVersion)
            library("bootDependencies", "org.springframework.boot", "spring-boot-dependencies").version(springBootVersion)
            library("cloudBom", "org.springframework.cloud", "spring-cloud-dependencies").version(springCloudVersion)
        }
        create("tracing") {
            val otelExporterVersion: String by settings

        }
        create("keycloak") {
            val keycloakVersion: String by settings
            val keycloakGroup = "org.keycloak"
            library("adminClient", keycloakGroup, "keycloak-admin-client").version(keycloakVersion)
            library("core", keycloakGroup, "keycloak-core").version(keycloakVersion)
            library("services", keycloakGroup, "keycloak-services").version(keycloakVersion)
        }
    }
}