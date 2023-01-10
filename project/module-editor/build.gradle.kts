import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    compileOnly(project(":project:common"))
    compileOnly(project(":project:common-impl"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    withType<ShadowJar> {
        // download
        relocate("com.github.benmanes.caffeine", "com.github.benmanes.caffeine_2_9_3")
    }
    build {
        dependsOn(shadowJar)
    }
}