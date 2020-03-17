/*
 *    Copyright 2019 Django Cass
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	id("org.springframework.boot") version "2.2.5.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	kotlin("jvm") version "1.3.70"
	kotlin("plugin.spring") version "1.3.70"
	kotlin("kapt") version "1.3.70"
	id("com.github.ben-manes.versions") version "0.27.0"
}
group = "dev.castive"
version = "0.4"
java.apply {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

val moduleName by extra("dev.castive.fav2")
val javaHome: String = System.getProperty("java.home")

repositories {
	maven(url = "https://mvn.v2.dcas.dev")
	maven(url = "https://jitpack.io")
	mavenCentral()
	jcenter()
}

val junitVersion: String by project
extra["springCloudVersion"] = "Hoxton.SR2"

dependencies {
	// standard library
	implementation(kotlin("stdlib-jdk8"))
	implementation(kotlin("reflect"))
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.4")

	implementation("com.sun.activation:javax.activation:1.2.0")

	// spring
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	kapt("org.springframework.boot:spring-boot-configuration-processor")
	implementation("org.springframework.cloud:spring-cloud-starter-config")

	implementation("com.github.djcass44:castive-utilities:v6.RC3") {
		exclude("org.springframework.boot", "spring-boot-starter-data-jpa")
	}
	implementation("com.github.djcass44:log2:4.1")
	implementation("org.jsoup:jsoup:1.12.1")
	implementation("com.google.guava:guava:28.2-jre")

	implementation("com.twelvemonkeys.imageio:imageio-core:3.5")
	implementation("com.twelvemonkeys.imageio:imageio-bmp:3.5")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.2")

	// swagger
	implementation("io.springfox:springfox-swagger2:2.9.2")
	implementation("io.springfox:springfox-swagger-ui:2.9.2")

	implementation("redis.clients:jedis:3.2.+")

	// testing
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude("org.junit.jupiter")
		exclude("org.junit.vintage")
	}
	testImplementation("org.jetbrains.kotlin:kotlin-test")
	testImplementation("org.hamcrest:hamcrest:2.2")

	testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
	testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

configure<JavaPluginConvention> {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}
tasks {
	wrapper {
		gradleVersion = "6.1"
		distributionType = Wrapper.DistributionType.ALL
	}
	withType<KotlinCompile>().all {
		kotlinOptions {
			freeCompilerArgs = listOf("-Xjsr305=strict")
			jvmTarget = "11"
		}
	}
	withType<Test> {
		useJUnitPlatform()
	}
	withType<BootJar> {
		archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
	}
}
