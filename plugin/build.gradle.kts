import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val taboolib_version: String by project

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
    implementation("io.izzel.taboolib:common-5:$taboolib_version")
    implementation("io.izzel.taboolib:module-chat:$taboolib_version")
    implementation("io.izzel.taboolib:module-configuration:$taboolib_version")
    implementation("io.izzel.taboolib:module-effect:$taboolib_version")
    implementation("io.izzel.taboolib:module-lang:$taboolib_version")
    implementation("io.izzel.taboolib:module-nms:$taboolib_version")
    implementation("io.izzel.taboolib:module-nms-util:$taboolib_version")
    implementation("io.izzel.taboolib:module-ui:$taboolib_version")
    implementation("io.izzel.taboolib:module-navigation:$taboolib_version")
    implementation("io.izzel.taboolib:platform-bukkit:$taboolib_version")
    implementation("com.eatthepath:fast-uuid:0.2.0")
    implementation("com.github.ben-manes.caffeine:caffeine:2.8.5")
    implementation("org.spongepowered:math:2.0.1")
    implementation(project(":project:common"))
    implementation(project(":project:common-impl"))
    implementation(project(":project:common-impl-nms"))
    implementation(project(":project:common-impl-nms-j17"))
    implementation(project(":project:module-bukkit"))
    implementation(project(":project:module-editor"))
    implementation(project(":project:module-language"))
    implementation(project(":project:module-legacy-api"))
    implementation(project(":project:module-test"))
    implementation(project(":project:api-data-serializer"))
}

tasks {
    withType<ShadowJar> {
        archiveClassifier.set("")
        exclude("META-INF/maven/**")
        exclude("META-INF/tf/**")
        exclude("module-info.java")
        relocate("taboolib", "ink.ptms.adyeshach.taboolib")
        relocate("com.github.benmanes.caffeine", "ink.ptms.adyeshach.taboolib.library.caffeine")
        relocate("org.checkerframework", "ink.ptms.adyeshach.taboolib.library.checkerframework")
        relocate("com.google.errorprone", "ink.ptms.adyeshach.taboolib.library.errorprone")
        relocate("com.eatthepath.uuid", "ink.ptms.adyeshach.taboolib.library.uuid")
        relocate("kotlin.", "kotlin1531.") {
            exclude("kotlin.Metadata")
        }
    }
    build {
        dependsOn(shadowJar)
    }
}