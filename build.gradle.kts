plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0"
    id("maven-publish")
}
group = "dev.kofeychi"
version = "1.0-SNAPSHOT"

val lwjglVersion = "3.3.6"

repositories {
    mavenCentral()
}
tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "dev.kofeychi.mcgui.test.BasicQuadApplication",
        )
    }
}
dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("it.unimi.dsi:fastutil:8.5.18")
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    var libs = listOf(
        "lwjgl",
        "lwjgl-glfw",
        "lwjgl-opengl",
        "lwjgl-stb"
    )

    libs.forEach {
        implementation("org.lwjgl", it)
        implementation("org.lwjgl", it, classifier = "natives-windows")
        implementation("org.lwjgl", it, classifier = "natives-linux")
    }

    implementation("org.joml:joml:1.10.8")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.jar) {
                artifactId = tasks.jar.get().archiveBaseName.get()
            }
        }
    }
}