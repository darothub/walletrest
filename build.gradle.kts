plugins {
    java
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
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
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation ("org.springframework.boot:spring-boot-starter-webflux")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("com.h2database:h2")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    jvmArgs(
        "-javaagent:${configurations.testRuntimeClasspath.get().files.find { it.name.contains("mockito-core") }?.absolutePath}"
    )
    useJUnitPlatform()

    testLogging {
        events("PASSED", "FAILED", "SKIPPED") // show these test events
        showStandardStreams = true            // show output from println and logs
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }

    afterSuite(KotlinClosure2<TestDescriptor, TestResult, Unit>({ desc, result ->
        // Only print summary at the top level suite
        if (desc.parent == null) {
            println("────────────────────────────────────────────")
            println("Test result: ${result.resultType}")
            println("Total: ${result.testCount}, Passed: ${result.successfulTestCount}, Failed: ${result.failedTestCount}, Skipped: ${result.skippedTestCount}")
            println("────────────────────────────────────────────")
        }
    }))
}
