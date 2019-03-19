import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.30-eap-45"
    maven
}
group = "dev.castive"
version = "0.1"


repositories {
    maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
    maven(url = "https://jitpack.io")
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("com.gitlab.django-sandbox:log2:8b941edd1a")
    implementation("org.jsoup:jsoup:1.11.3")
    implementation("com.squareup.okhttp3:okhttp:3.14.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.2.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.2.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.2.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
tasks.withType<Test> {
    useJUnitPlatform()
}