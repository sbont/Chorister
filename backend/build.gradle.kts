import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    val kotlinVersion = "2.0.21"

    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jetbrains.kotlin.plugin.noarg") version kotlinVersion
    id("org.liquibase.gradle") version "2.2.0"

    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    application
}

apply(plugin = "kotlin-jpa")
apply(plugin = "java")
apply(plugin = "io.spring.dependency-management")

application {
    mainClass.set("nl.stevenbontenbal.chorister.ChoristerApplicationKt")
}

group = "nl.stevenbontenbal"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

liquibase {
    activities.register("main")
    runList = "main"
}

dependencies {
    "kapt"("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation("aws.sdk.kotlin:s3:1.3.56")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jboss.logging:jboss-logging")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.10.1")
    implementation("org.modelmapper:modelmapper:3.1.1")
    implementation("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.zalando:logbook-spring-boot-starter:2.16.0")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.springframework.boot:spring-boot-properties-migrator")

    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
        exclude(module = "mockito-core")
    }

    val kotestVersion = "5.9.1"
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    liquibaseRuntime("info.picocli:picocli:4.7.3")
    liquibaseRuntime("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
    liquibaseRuntime("org.liquibase:liquibase-core:4.20.0")
    liquibaseRuntime("org.liquibase:liquibase-groovy-dsl:3.0.3")
    liquibaseRuntime("org.liquibase.ext:liquibase-hibernate6:4.23.0")
    liquibaseRuntime("org.postgresql:postgresql:42.7.2")
    liquibaseRuntime("org.springframework.boot:spring-boot-starter-data-jpa")
    liquibaseRuntime(sourceSets.getByName("main").output)
    liquibaseRuntime(files("src/main"))
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.set(listOf("-Xjsr305=strict", "-Xjvm-default=all"))
        jvmTarget.set(JvmTarget.JVM_21)
        javaParameters.set(true)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.getByName<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    sourceResources(sourceSets["main"])
}

tasks.withType<BootJar> {
    archiveFileName.set("chorister.jar")
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}
