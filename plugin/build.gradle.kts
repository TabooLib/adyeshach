import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val taboolib_version: String by project

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
    implementation("io.izzel.taboolib:module-metrics:$taboolib_version")
    implementation("io.izzel.taboolib:platform-bukkit:$taboolib_version")
    implementation("io.izzel.taboolib:expansion-command-helper:$taboolib_version")
    implementation(project(":project:common", configuration = "shadow"))
    implementation(project(":project:common-impl", configuration = "shadow"))
    implementation(project(":project:common-impl-nms", configuration = "shadow"))
    implementation(project(":project:common-impl-nms-j17"))
    implementation(project(":project:compat-beton-quest"))
    implementation(project(":project:compat-germ-engine"))
    implementation(project(":project:compat-model-engine-v2"))
    // 谁能兼容谁来兼容，我不会
    // implementation(project(":project:compat-model-engine-v3"))
    implementation(project(":project:module-bukkit"))
    implementation(project(":project:module-editor", configuration = "shadow"))
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
        // 移除测试单元
        exclude("**/test/**")
        // taboolib
        relocate("taboolib", "ink.ptms.adyeshach.taboolib")
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