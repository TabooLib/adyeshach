import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    compileOnly("ink.ptms.core:v12004:12004:mapped")
    compileOnly("net.citizensnpcs:Citizens:2.0.18")
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