plugins {
    id("org.gradle.java")
    id("org.gradle.maven-publish")
    id("org.jetbrains.kotlin.jvm") version "1.5.31" apply false
}

subprojects {
    apply<JavaPlugin>()
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
    }
    dependencies {
        compileOnly(kotlin("stdlib"))
        compileOnly("ink.ptms:nms-all:1.0.0")
        compileOnly("com.google.code.gson:gson:2.8.5")
        compileOnly("com.google.guava:guava:21.0")
        compileOnly("ink.ptms.core:v11900:11900:mapped")
        compileOnly("ink.ptms.core:v11900:11900-minimize:universal")
    }
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.tabooproject.org/repository/releases")
            credentials {
                username = project.findProperty("taboolibUsername").toString()
                password = project.findProperty("taboolibPassword").toString()
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            groupId = project.group.toString()
        }
    }
}

gradle.buildFinished {
    buildDir.deleteRecursively()
}