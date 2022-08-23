import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import com.google.protobuf.gradle.*


plugins {
	id("org.springframework.boot") version "2.7.1"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	id("com.google.protobuf") version "0.8.19"
	idea
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

buildscript {
	repositories {
		gradlePluginPortal()
	}
	dependencies {
		classpath("com.google.protobuf:protobuf-gradle-plugin:0.8.19")
	}
}

dependencies {
	val grpcVersion = "1.48.0"
	val protobufJavaVersion = "3.21.4"
	val reactiveGrpc = "1.2.3"

	implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.10")

	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

	// Reactive
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")

	// Spring Boot Starter Data Redis Reactive
	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive:2.7.3")
	implementation("org.springframework.boot:spring-boot-starter-cache:2.7.3")

	// gRPC
	implementation("io.grpc:grpc-netty:$grpcVersion")
	implementation("io.grpc:grpc-stub:$grpcVersion")
	implementation("io.grpc:grpc-protobuf:$grpcVersion")
	implementation("io.grpc:grpc-kotlin-stub:1.3.0")
	implementation("com.google.protobuf:protobuf-gradle-plugin:0.8.19")
	implementation("com.google.protobuf:protobuf-java:$protobufJavaVersion")
	implementation("com.google.protobuf:protobuf-java-util:$protobufJavaVersion")
	implementation("javax.annotation:javax.annotation-api:1.3.2")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	// Reactive gRPC
	implementation("com.salesforce.servicelibs:reactor-grpc:$reactiveGrpc")
	implementation("com.salesforce.servicelibs:reactive-grpc-common:$reactiveGrpc")
	implementation("com.salesforce.servicelibs:reactor-grpc-stub:$reactiveGrpc")

	// NATS client
	implementation("io.nats:jnats:2.15.6")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")

	testImplementation("org.testcontainers:testcontainers:1.17.2")
	testImplementation("org.testcontainers:junit-jupiter:1.17.2")
}


tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()

	testLogging {
		events(PASSED, SKIPPED, FAILED)
	}
}

protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc:3.14.0"
	}

	plugins {
		id("grpc") {
			artifact = "io.grpc:protoc-gen-grpc-java:1.48.1"
		}
		id("reactor") {
			artifact = "com.salesforce.servicelibs:reactor-grpc:1.2.3"
		}
	}

	generateProtoTasks {
		ofSourceSet("main").forEach {
			it.plugins {
				id("grpc")
				id("reactor")
			}
		}
	}
}
