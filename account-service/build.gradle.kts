plugins {
	alias(spring.plugins.dependencyManagement)
	alias(spring.plugins.nativeBuild)
}

version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(project(":contract"))
	implementation(project(":security"))
	implementation(keycloak.adminClient)
	implementation(keycloak.core)
	implementation(keycloak.services)
	implementation("org.springframework.boot","spring-boot-starter-actuator")
	implementation("org.springframework.boot","spring-boot-starter-validation")
	implementation("org.springframework.boot","spring-boot-starter-webflux")
	implementation("io.micrometer","micrometer-tracing")
	implementation("io.micrometer","micrometer-tracing-bridge-otel")
	implementation("io.opentelemetry","opentelemetry-exporter-otlp")
	implementation("com.fasterxml.jackson.module","jackson-module-kotlin")
	implementation("io.projectreactor.kotlin","reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin","kotlin-reflect")
	implementation("org.jetbrains.kotlinx","kotlinx-coroutines-reactor")
	runtimeOnly("io.micrometer","micrometer-registry-prometheus")
	annotationProcessor("org.springframework.boot","spring-boot-configuration-processor")
	testImplementation("org.springframework.boot","spring-boot-starter-test")
	testImplementation("io.projectreactor","reactor-test")
	testImplementation("org.springframework.security","spring-security-test")
}
