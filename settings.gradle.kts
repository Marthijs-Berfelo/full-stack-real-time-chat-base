rootProject.name = "full-stack-real-time-chat"

include(
    "contract",
    "security",
    "account-service",
    "message-service",
    "user-service"
)

pluginManagement {
    val kotlinVersion: String by settings
    val springBootVersion: String by settings
    val springDependenciesVersion: String by settings
    val graalVmVersion: String by settings
    resolutionStrategy {
        eachPlugin {
            when (requested.id.namespace) {
                "org.springframework" -> useVersion(springBootVersion)
                "io.spring" -> useVersion(springDependenciesVersion)
                "org.graalvm.buildtools" -> useVersion(graalVmVersion)
                "org.jetbrains.kotlin.plugin" -> useVersion(kotlinVersion)
                "org.jetbrains.kotlin" -> useVersion(kotlinVersion)
            }
        }
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("spring") {
            val springBootVersion: String by settings
            val springCloudVersion: String by settings
            val springCloudSleuthVersion: String by settings
            library("bootParent", "org.springframework.boot", "spring-boot-starter-parent").version(springBootVersion)
            library("bootDependencies", "org.springframework.boot", "spring-boot-dependencies").version(springBootVersion)
            library("cloudBom", "org.springframework.cloud", "spring-cloud-dependencies").version(springCloudVersion)
            library("sleuth", "org.springframework.cloud", "spring-cloud-sleuth-otel").version(springCloudSleuthVersion)
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
        create("k8s") {
            val kubernetesClientVersion: String by settings
            library("client", "io.kubernetes", "client-java-spring-integration").version(kubernetesClientVersion)
        }
    }
}