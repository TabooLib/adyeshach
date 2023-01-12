import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val taboolib_version: String by project

plugins {
    `maven-publish`
    java
    id("org.jetbrains.kotlin.jvm") version "1.5.31" apply false
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

subprojects {
    apply<JavaPlugin>()
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "maven-publish")
    apply(plugin = "com.github.johnrengelman.shadow")

    repositories {
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://repo.spongepowered.org/maven") }
        maven {
            url = uri("http://ptms.ink:8081/repository/releases/")
            isAllowInsecureProtocol = true
        }
    }
    dependencies {
        compileOnly(kotlin("stdlib"))
        // taboolib
        compileOnly("io.izzel.taboolib:common:$taboolib_version")
        compileOnly("io.izzel.taboolib:common-5:$taboolib_version")
        compileOnly("io.izzel.taboolib:module-chat:$taboolib_version")
        compileOnly("io.izzel.taboolib:module-configuration:$taboolib_version")
        compileOnly("io.izzel.taboolib:module-database:$taboolib_version")
        compileOnly("io.izzel.taboolib:module-effect:$taboolib_version")
        compileOnly("io.izzel.taboolib:module-lang:$taboolib_version")
        compileOnly("io.izzel.taboolib:module-nms:$taboolib_version")
        compileOnly("io.izzel.taboolib:module-nms-util:$taboolib_version")
        compileOnly("io.izzel.taboolib:module-kether:$taboolib_version")
        compileOnly("io.izzel.taboolib:module-ui:$taboolib_version")
        compileOnly("io.izzel.taboolib:module-navigation:$taboolib_version")
        compileOnly("io.izzel.taboolib:module-metrics:$taboolib_version")
        compileOnly("io.izzel.taboolib:platform-bukkit:$taboolib_version")
        compileOnly("io.izzel.taboolib:expansion-command-helper:$taboolib_version")
        // server
        compileOnly("ink.ptms.core:v11604:11604")
        compileOnly("ink.ptms:nms-all:1.0.0")
        compileOnly("com.google.code.gson:gson:2.8.5")
        compileOnly("com.google.guava:guava:21.0")
        // include
        compileOnly("com.eatthepath:fast-uuid:0.2.0")
        compileOnly("org.spongepowered:math:2.0.1")
        // download
        compileOnly("com.github.ben-manes.caffeine:caffeine:2.9.3")
    }
    java {
        withSourcesJar()
    }
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjvm-default=all", "-Xextended-compiler-checks")
        }
    }
    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

gradle.buildFinished {
    buildDir.deleteRecursively()
}

subprojects
    .filter { it.name != "project" && it.name != "plugin" }
    .forEach { proj ->
        proj.publishing { applyToSub(proj) }
    }

fun PublishingExtension.applyToSub(subProject: Project) {
    repositories {
        maven("http://ptms.ink:8081/repository/releases") {
            isAllowInsecureProtocol = true
            credentials {
                username = project.findProperty("taboolibUsername").toString()
                password = project.findProperty("taboolibPassword").toString()
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
        mavenLocal()
    }
    publications {
        create<MavenPublication>("maven") {
            artifactId = subProject.name
            groupId = "ink.ptms.adyeshach"
            version = project.version.toString()
            artifact(subProject.tasks["kotlinSourcesJar"])
            artifact(subProject.tasks["shadowJar"]) {
                classifier = null
            }
            println("> Apply \"$groupId:$artifactId:$version\"")
        }
    }
}