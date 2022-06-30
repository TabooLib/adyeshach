import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
    implementation("com.github.ben-manes.caffeine:caffeine:2.8.5")
    implementation(project(":project:common"))
    implementation(project(":project:common-api"))
    implementation(project(":project:common-default-impl"))
    implementation(project(":project:module-bukkit"))
    implementation(project(":project:module-editor"))
    implementation(project(":project:module-language"))
    implementation(project(":project:module-legacy-api"))
    implementation(project(":project:module-test"))
}

tasks {
    withType<ShadowJar> {
        archiveClassifier.set("")
        exclude("META-INF/maven/**")
        exclude("META-INF/tf/**")
        exclude("module-info.java")
        relocate("com.github.benmanes.caffeine", "ink.ptms.adyeshach.taboolib.library.caffeine")
        relocate("org.checkerframework", "ink.ptms.adyeshach.taboolib.library.checkerframework")
        relocate("com.google.errorprone", "ink.ptms.adyeshach.taboolib.library.errorprone")
    }
    build {
        dependsOn(shadowJar)
    }
}