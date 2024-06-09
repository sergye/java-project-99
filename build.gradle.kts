plugins {
	application
	checkstyle
	jacoco
	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.4"
	id("com.github.ben-manes.versions") version "0.49.0"
	id("io.freefair.lombok") version "8.6"
	id("io.sentry.jvm.gradle") version "4.7.1"
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

application {
	mainClass.set("hexlet.code.AppApplication")
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-devtools")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-configuration-processor")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("net.datafaker:datafaker:2.0.2")
	implementation("org.instancio:instancio-junit:3.3.1")
	implementation("org.openapitools:jackson-databind-nullable:0.2.6")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
	runtimeOnly("com.h2database:h2")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation(platform("org.junit:junit-bom:5.10.0"))
	testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
	testImplementation("net.javacrumbs.json-unit:json-unit-assertj:3.2.2")
	testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
	finalizedBy(tasks.jacocoTestReport)
}
tasks.jacocoTestReport {
	dependsOn(tasks.test)
}

tasks.jacocoTestReport {
	reports {
		xml.required = true
	}
}

sentry {
	includeSourceContext = true

	org = "serhii-e1p"
	projectName = "java-spring-boot"
	authToken = System.getenv("SENTRY_AUTH_TOKEN")
}

tasks.sentryBundleSourcesJava {
	enabled = System.getenv("SENTRY_AUTH_TOKEN") != null
}
