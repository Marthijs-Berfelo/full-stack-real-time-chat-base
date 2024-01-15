import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(spring.plugins.boot) apply false
    alias(spring.plugins.dependencyManagement) apply false
    alias(spring.plugins.nativeBuild) apply false
    alias(kt.plugins.spring) apply false
    alias(kt.plugins.jvm) apply false
}

val javaVersion = JavaVersion.valueOf(lib.versions.java.get())
val springBootPlugin = spring.plugins.boot.get().pluginId
val kotlinJvmPlugin = kt.plugins.jvm.get().pluginId
val kotlinSpringPlugin = kt.plugins.spring.get().pluginId


allprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin = springBootPlugin)
    apply(plugin = kotlinJvmPlugin)
    apply(plugin = kotlinSpringPlugin)

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs += "-Xjsr305=strict"
                jvmTarget = javaVersion.majorVersion
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
