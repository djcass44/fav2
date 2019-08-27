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

import org.ajoberstar.grgit.Grgit
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.sonarqube.gradle.SonarQubeTask

plugins {
	kotlin("jvm") version "1.3.31"
	java
	application
	jacoco
	id("org.beryx.jlink") version "2.4.3"
	id("org.sonarqube") version "2.7.1"
	id("org.ajoberstar.grgit") version "1.7.2"
}
group = "dev.castive"
version = "0.3"
val moduleName by extra("dev.castive.fav2")
val javaHome = System.getProperty("java.home")

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
	maven(url = "https://dl.bintray.com/nitram509/jbrotli/")
}

val junitVersion: String by project

dependencies {
	implementation(kotlin("stdlib-jdk8:modular"))
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.2")

	implementation("com.github.djcass44:log2:7df051d775")
	implementation("org.jsoup:jsoup:1.11.3")
	implementation("com.squareup.okhttp3:okhttp:3.14.0")

	implementation("com.twelvemonkeys.imageio:imageio-bmp:3.4.1")

	implementation("io.javalin:javalin:3.2.0")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")
	implementation("org.slf4j:slf4j-simple:1.7.26")

	implementation("com.google.guava:guava:28.0-jre")

	testImplementation("org.jetbrains.kotlin:kotlin-test:1.3.31")

	testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
	testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
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
	withType<JacocoReport> {
		reports {
			xml.isEnabled = true
		}
	}
	withType<SonarQubeTask> { dependsOn("test", "jacocoTestReport") }
}
jacoco {
	toolVersion = "0.8.4"
}
val codeCoverageReport by tasks.creating(JacocoReport::class) {
	dependsOn("test")
}

sonarqube {
	val git = runCatching { Grgit.open(project.rootDir) }.getOrNull()
	// Don't run an analysis if we can't get git context
	val name = (if(git == null) null else runCatching { git.branch.current.name }.getOrNull())
	val target = when(name) {
		null -> null
		"develop" -> "master"
		else -> "develop"
	}
	val branch = if(name != null && target != null) Pair(name, target) else null
	this.isSkipProject = branch == null
	properties{
		property("sonar.projectKey", "djcass44:fav2")
		property("sonar.projectName", "djcass44/fav2")
//		if(branch != null) {
//			property("sonar.branch.name", branch.first)
//			property("sonar.branch.target", branch.second)
//		}
		property("sonar.jacoco.xmlReportPaths", "$projectDir/build/test-results/test")
	}
}
