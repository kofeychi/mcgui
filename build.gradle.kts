plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.0"
}
group = "dev.kofeychi"
version = "1.0-SNAPSHOT"
val lwjglVersion = "3.3.6"
val lwjglNatives = "natives-windows"
repositories {
    mavenCentral()
}
val shadowBundle: Configuration by configurations.creating {
    // Наследуем от implementation, чтобы зависимости были доступны при компиляции
    configurations.implementation.get().extendsFrom(this)
}
tasks.shadowJar {
    configurations = listOf(shadowBundle)
    mergeServiceFiles()
}
dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    shadowBundle("com.google.code.gson:gson:2.10.1")
    implementation("it.unimi.dsi:fastutil:8.5.18")
    shadowBundle("it.unimi.dsi:fastutil:8.5.18")
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    shadowBundle(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    var libs = listOf(
        "lwjgl",
        "lwjgl-glfw",
        "lwjgl-opengl",
        "lwjgl-stb"
    )

    libs.forEach {
        implementation("org.lwjgl", it)
        shadowBundle("org.lwjgl", it)
        runtimeOnly("org.lwjgl", it, classifier = lwjglNatives)
    }

    implementation("org.joml:joml:1.10.8")
}

tasks.register<JavaExec>("runFromJar") {
    dependsOn(tasks.jar)

    val jarTask = tasks.jar.get()
    classpath = files(jarTask.archiveFile) + sourceSets.main.get().runtimeClasspath

    mainClass.set("dev.kofeychi.mcgui.DevJarEntrypoint")
}