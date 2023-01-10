import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val taboolib_version: String by project

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
    implementation("io.izzel.taboolib:common-5:$taboolib_version")
    implementation("io.izzel.taboolib:module-chat:$taboolib_version")
    implementation("io.izzel.taboolib:module-configuration:$taboolib_version")
    implementation("io.izzel.taboolib:module-database:$taboolib_version")
    implementation("io.izzel.taboolib:module-effect:$taboolib_version")
    implementation("io.izzel.taboolib:module-lang:$taboolib_version")
    implementation("io.izzel.taboolib:module-kether:$taboolib_version")
    implementation("io.izzel.taboolib:module-nms:$taboolib_version")
    implementation("io.izzel.taboolib:module-nms-util:$taboolib_version")
    implementation("io.izzel.taboolib:module-ui:$taboolib_version")
    implementation("io.izzel.taboolib:module-navigation:$taboolib_version")
    implementation("io.izzel.taboolib:platform-bukkit:$taboolib_version")
    implementation("io.izzel.taboolib:expansion-command-helper:$taboolib_version")
    implementation("com.eatthepath:fast-uuid:0.2.0")
    implementation("org.spongepowered:math:2.0.1")
    implementation(project(":project:common"))
    implementation(project(":project:common-impl"))
    implementation(project(":project:common-impl-nms"))
    implementation(project(":project:common-impl-nms-j17"))
    implementation(project(":project:compat-beton-quest"))
    implementation(project(":project:compat-germ-engine"))
    implementation(project(":project:compat-model-engine-v2"))
    // 谁能兼容谁来兼容，我不会
    // implementation(project(":project:compat-model-engine-v3"))
    implementation(project(":project:module-bukkit"))
    implementation(project(":project:module-editor"))
    implementation(project(":project:module-language"))
    implementation(project(":project:module-legacy-api"))
    // 暂未使用
    // implementation(project(":project:api-alkaid-mongodb"))
    implementation(project(":project:api-data-serializer"))
}

tasks {
    withType<ShadowJar> {
        archiveClassifier.set("")
        exclude("META-INF/maven/**")
        exclude("META-INF/tf/**")
        exclude("module-info.java")

        // include
        relocate("taboolib", "ink.ptms.adyeshach.taboolib")
        relocate("com.eatthepath.uuid", "ink.ptms.adyeshach.taboolib.library.uuid")
        relocate("org.spongepowered.math", "ink.ptms.adyeshach.taboolib.library.math")

        // download
        relocate("com.github.benmanes.caffeine", "com.github.benmanes.caffeine_2_9_3")
        relocate("org.mongodb", "org.mongodb_3_12_11")

        // kotlin
        relocate("kotlin.", "kotlin1531.") {
            exclude("kotlin.Metadata")
        }
    }
    kotlinSourcesJar {
        // include subprojects
        rootProject.subprojects.forEach { from(it.sourceSets["main"].allSource) }
    }
    build {
        dependsOn(shadowJar)
    }
}

publishing {
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
            artifactId = "all"
            groupId = "ink.ptms.adyeshach"
            version = project.version.toString()
            artifact(tasks["kotlinSourcesJar"])
            artifact(tasks.named<ShadowJar>("shadowJar"))
            println("> Apply \"$groupId:$artifactId:$version\"")
        }
    }
}