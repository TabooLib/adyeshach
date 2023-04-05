import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    compileOnly("com.eatthepath:fast-uuid:0.2.0")
    compileOnly("org.mongodb:mongo-java-driver:3.12.11")
    compileOnly("ink.ptms.core:v11900:11900:mapped")
    compileOnly("ink.ptms.core:v11900:11900:universal")
    compileOnly("ink.ptms.core:v11904:11904:mapped")
    compileOnly("net.md-5:bungeecord-chat:1.17")
    compileOnly("io.netty:netty-all:4.1.86.Final")
    compileOnly("public:ServerTours:1.6.5")
    compileOnly("public:ServerTours2:2.0.3")
    compileOnly(project(":project:common"))
    // include
    implementation("org.spongepowered:math:2.0.1")
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
        // download
        relocate("com.github.benmanes.caffeine", "com.github.benmanes.caffeine_2_9_3")
        relocate("org.mongodb", "org.mongodb_3_12_11")
    }
    build {
        dependsOn(shadowJar)
    }
}