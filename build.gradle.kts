plugins {
    id("java")
}
group = "dev.kofeychi"
version = "1.0-SNAPSHOT"

val lwjglVersion = "3.3.6"
val lwjglNatives = "natives-windows"

repositories {
    mavenCentral()
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
        runtimeOnly("org.lwjgl", it, classifier = lwjglNatives)
    }

    implementation("org.joml:joml:1.10.8")
}