plugins {
    id("java")
    id("maven")
    id("maven-publish")
}

group = "com.github.avbhatt"
version = "0.0.1"

repositories {
    mavenCentral()
    uri("https://jitpack.io")
}

dependencies {
    compile(group = "org.reflections", name = "reflections", version = "0.9.11")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.2")
    testCompile(group = "org.mockito", name = "mockito-core", version = "2.23.0")
    testCompile(group = "org.assertj", name = "assertj-core", version = "3.12.0")
}

tasks.jar {
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}
