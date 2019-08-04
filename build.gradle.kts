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
	kotlin("jvm") version "1.3.31"
	java
	application
	id("org.beryx.jlink") version "2.4.3"
}
group = "dev.castive"
version = "0.3"
val moduleName by extra("dev.castive.fav2")
val javaHome = System.getProperty("java.home")

application {
	mainClassName = "dev.castive.fav2.http.EntrypointKt"
}

repositories {
	maven(url = "https://jitpack.io")
	mavenCentral()
	jcenter()
	maven(url = "https://dl.bintray.com/nitram509/jbrotli/")
}

dependencies {
	implementation(kotlin("stdlib-jdk8:modular"))
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.2")

	implementation("com.github.djcass44:log2:develop-SNAPSHOT")
	implementation("org.jsoup:jsoup:1.11.3")
	implementation("com.squareup.okhttp3:okhttp:3.14.0")

	implementation("com.twelvemonkeys.imageio:imageio-bmp:3.4.1")

	implementation("io.javalin:javalin:3.2.0")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")
	implementation("org.slf4j:slf4j-simple:1.7.26")

	testImplementation("org.junit.jupiter:junit-jupiter-api:5.2.0")
	testImplementation("org.junit.jupiter:junit-jupiter-params:5.2.0")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.2.0")
}
configure<JavaPluginConvention> {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}
tasks {
	withType<KotlinCompile>().all {
		kotlinOptions.jvmTarget = "11"
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