plugins {
    `maven-publish`
}

taboolib {
    description {
        name(rootProject.name)
        contributors {
            name("IzzelAliz")
            name("坏黑")
            name("Arasple")
            name("zhanshi123")
        }
        dependencies {
            name("Zaphkiel").optional(true)
            name("Citizens").optional(true)
            name("ServerNPC").optional(true)
            name("ModelEngine").optional(true)
            name("BetonQuest").optional(true)
        }
    }
    // asm
    relocate("org.objectweb.asm.", "org.objectweb.asm9.")
    // include
    relocate("com.eatthepath.uuid.", "ink.ptms.adyeshach.taboolib.library.uuid.")
    relocate("org.spongepowered.math.", "ink.ptms.adyeshach.taboolib.library.math.")
    // download
    // relocate("org.mongodb.", "org.mongodb_3_12_11.")
    relocate("com.github.benmanes.caffeine.", "com.github.benmanes.caffeine_2_9_3.")
}

dependencies {
    taboo("com.eatthepath:fast-uuid:0.2.0")
    taboo("org.spongepowered:math:2.0.1")
}

val distDir = rootProject.layout.projectDirectory.dir("dist")

tasks {
    jar {
        archiveBaseName.set(rootProject.name)
//        destinationDirectory.set(distDir)
        rootProject.subprojects.forEach { from(it.sourceSets["main"].output) }
    }
    kotlinSourcesJar {
        archiveBaseName.set(rootProject.name)
//        destinationDirectory.set(distDir)
        rootProject.subprojects.forEach { from(it.sourceSets["main"].allSource) }
    }
    withType<Jar>().configureEach {
//        destinationDirectory.set(distDir)
    }
}

publishing {
    repositories {
        mavenLocal()
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
        // API 发布配置
        create<MavenPublication>("api") {
            groupId = "ink.ptms.adyeshach"
            artifactId = "api"
            // 使用 taboolibBuildApi 任务的输出
            artifact(distDir.asFile.resolve("${rootProject.name}-${rootProject.version}-api.jar"))
            // 添加 sources jar
            artifact(tasks.named("kotlinSourcesJar")) {
                classifier = "sources"
            }
        }
    }
}