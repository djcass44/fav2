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

plugins {
	kotlin("jvm") version "1.3.50"
	java
	application
	id("org.beryx.jlink") version "2.16.0"
	id("com.github.ben-manes.versions") version "0.26.0"
}
group = "dev.castive"
version = "0.3"

val moduleName by extra("dev.castive.fav2")
val javaHome: String = System.getProperty("java.home")

application {
	mainClassName = "dev.castive.fav2.http.EntrypointKt"
	applicationDefaultJvmArgs = listOf(
		"-Djava.util.logging.config.file=src/main/resources/logging.properties"
	)
}

repositories {
	maven(url = "https://jitpack.io")
	mavenCentral()
	jcenter()
}

val kotlinVersion: String by project
val junitVersion: String by project
val jettyVersion: String by project

dependencies {
	// standard library
	implementation(kotlin("stdlib-jdk8:$kotlinVersion"))
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
	implementation("com.sun.activation:javax.activation:1.2.0")

	implementation("com.github.djcass44:log2:3.4")
	implementation("org.jsoup:jsoup:1.12.1")
	implementation("com.squareup.okhttp3:okhttp:3.14.0")

	implementation("com.twelvemonkeys.imageio:imageio-bmp:3.4.2")

	implementation("io.javalin:javalin:3.5.0")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")
	implementation("org.slf4j:slf4j-simple:1.7.26")

	// http2
	implementation("org.eclipse.jetty.http2:http2-server:$jettyVersion")
	implementation("org.eclipse.jetty:jetty-alpn-conscrypt-server:$jettyVersion")
	implementation("org.eclipse.jetty.alpn:alpn-api:1.1.3.v20160715")
//	implementation("org.mortbay.jetty.alpn:alpn-boot:8.1.13.v20181017")

	implementation("com.google.guava:guava:28.1-jre")

	// swagger
	implementation("io.swagger.core.v3:swagger-core:2.0.8")
	implementation("org.webjars:swagger-ui:3.23.8")

	// testing
	testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")

	testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
	testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}
configure<JavaPluginConvention> {
	sourceCompatibility = JavaVersion.VERSION_12
	targetCompatibility = JavaVersion.VERSION_12
}
tasks {
	withType<KotlinCompile>().all {
		kotlinOptions.jvmTarget = "12"
	}
	withType<JavaCompile>().all {
		inputs.property("moduleName", moduleName)
		doFirst {
			options.compilerArgs = listOf(
				"--module-path", classpath.asPath,
				"--patch-module", "$moduleName=${sourceSets["main"].output.asPath}"
			)
			classpath = files()
		}
	}
	withType<Test> {
		useJUnitPlatform()
	}
}
