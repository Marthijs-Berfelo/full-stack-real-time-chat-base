import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management") apply false
    id("org.graalvm.buildtools.native") apply false
    kotlin("plugin.spring") apply false
    kotlin("jvm") apply false
}

group = "com.example.chat"
version = "0.0.1-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin ="org.springframework.boot")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")


    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs += "-Xjsr305=strict"
                jvmTarget = "17"
            }
        }
        withType<Test> {
            useJUnitPlatform()
            testLogging {
                exceptionFormat = TestExceptionFormat.FULL
                showCauses = true
                showExceptions = true
                showStackTraces = true
                events(TestLogEvent.STARTED, TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
            }
        }
    }
}
