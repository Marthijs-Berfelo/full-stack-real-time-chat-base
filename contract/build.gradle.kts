plugins {
    `java-library`
    `maven-publish`
    alias(spring.plugins.dependencyManagement)
}

version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.valueOf(lib.versions.java.get())
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            group = parent?.group ?: project.group
            artifactId = project.name
            version = (parent?.version ?: project.version).toString()

            pom {
                name.set(project.name)
            }
        }
    }
}