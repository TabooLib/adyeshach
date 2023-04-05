import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    compileOnly("ink.ptms.core:v11900:11900:universal")
    compileOnly("ink.ptms.core:v11904:11904:mapped")
    // include
    implementation("com.eatthepath:fast-uuid:0.2.0")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    withType<ShadowJar> {
        // include
        relocate("com.eatthepath.uuid", "ink.ptms.adyeshach.taboolib.library.uuid")
        relocate("org.spongepowered.math", "ink.ptms.adyeshach.taboolib.library.math")
    }
    build {
        dependsOn(shadowJar)
    }
}