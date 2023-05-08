import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.6"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("org.jetbrains.kotlin.plugin.noarg") version "1.8.0"
	kotlin("jvm") version "1.8.0"
	kotlin("plugin.spring") version "1.8.0"
	kotlin("plugin.jpa") version "1.8.0"
	kotlin("plugin.allopen") version "1.8.0"
	kotlin("kapt") version "1.8.0"
	application
	war
}

apply(plugin = "kotlin-jpa")

application {
	mainClass.set("nl.stevenbontenbal.chorister.ChoristerApplication")
}

group = "nl.stevenbontenbal"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-rest")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.modelmapper:modelmapper:3.1.1")
    implementation("junit:junit:4.13.2")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	developmentOnly("org.zalando:logbook-spring-boot-starter:2.16.0")
//	developmentOnly("org.zalando:logbook-servlet")
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

allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.Embeddable")
	annotation("javax.persistence.MappedSuperclass")
}
