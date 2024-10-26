import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	id("org.springframework.boot") version "3.0.6"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("org.jetbrains.kotlin.plugin.noarg") version "2.0.21"
	id("org.liquibase.gradle") version "2.2.0"
	kotlin("jvm") version "2.0.21"
	kotlin("plugin.spring") version "2.0.21"
	kotlin("plugin.jpa") version "2.0.21"
	kotlin("plugin.allopen") version "2.0.21"
	kotlin("kapt") version "2.0.21"
	application
}

apply(plugin = "kotlin-jpa")
apply(plugin = "java")

application {
	mainClass.set("nl.stevenbontenbal.chorister.ChoristerApplicationKt")
}

group = "nl.stevenbontenbal"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

liquibase {
	activities.register("main")
	runList = "main"
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-rest")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.postgresql:postgresql")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.modelmapper:modelmapper:3.1.1")
    implementation("junit:junit:4.13.2")
	implementation("org.jboss.logging:jboss-logging")
	implementation("aws.sdk.kotlin:s3:1.3.56")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	developmentOnly("org.zalando:logbook-spring-boot-starter:2.16.0")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.springframework.boot:spring-boot-properties-migrator")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(module = "junit")
		exclude(module = "mockito-core")
	}
	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("io.mockk:mockk:1.13.4")
	testImplementation("com.ninja-squad:springmockk:4.0.2")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	"kapt"("org.springframework.boot:spring-boot-configuration-processor")
	liquibaseRuntime("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
	liquibaseRuntime("org.liquibase:liquibase-core:4.20.0")
	liquibaseRuntime("org.liquibase:liquibase-groovy-dsl:3.0.3")
	liquibaseRuntime("org.liquibase.ext:liquibase-hibernate6:4.23.0")
	liquibaseRuntime("info.picocli:picocli:4.7.3")
	liquibaseRuntime("org.postgresql:postgresql:42.7.2")
	liquibaseRuntime("org.springframework.boot:spring-boot-starter-data-jpa")
	liquibaseRuntime(sourceSets.getByName("main").output)
	liquibaseRuntime(files("src/main"))
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all")
		jvmTarget = "17"
		javaParameters = true
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
