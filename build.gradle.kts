import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val taboolib_version: String by project

plugins {
    `maven-publish`
    java
    id("org.jetbrains.kotlin.jvm") version "1.5.31" apply false
}

subprojects {
    apply<JavaPlugin>()
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "maven-publish")

    repositories {
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://repo.spongepowered.org/maven") }
        maven {
            url = uri("http://ptms.ink:8081/repository/releases/")
            isAllowInsecureProtocol = true
        }
    }
    dependencies {
        compileOnly("io.izzel.taboolib:common:$taboolib_version")
        compileOnly("io.izzel.taboolib:common-5:$taboolib_version")
        compileOnly("io.izzel.taboolib:module-chat:$taboolib_version")
        compileOnly("io.izzel.taboolib:module-configuration:$taboolib_version")
        compileOnly("io.izzel.taboolib:module-database:$taboolib_version")
        compileOnly("io.izzel.taboolib:module-effect:$taboolib_version")
        compileOnly("io.izzel.taboolib:module-lang:$taboolib_version")
        compileOnly("io.izzel.taboolib:module-nms-util:$taboolib_version")
        compileOnly("io.izzel.taboolib:module-nms:$taboolib_version")
        compileOnly("io.izzel.taboolib:module-nms-util:$taboolib_version")
        compileOnly("io.izzel.taboolib:module-kether:$taboolib_version")
        compileOnly("io.izzel.taboolib:module-ui:$taboolib_version")
        compileOnly("io.izzel.taboolib:module-navigation:$taboolib_version")
        compileOnly("io.izzel.taboolib:platform-bukkit:$taboolib_version")
        compileOnly("io.izzel.taboolib:expansion-command-helper:$taboolib_version")
        compileOnly("ink.ptms.core:v11604:11604")
        compileOnly("ink.ptms:nms-all:1.0.0")
        compileOnly("com.google.code.gson:gson:2.8.5")
        compileOnly("com.google.guava:guava:21.0")
        compileOnly("com.eatthepath:fast-uuid:0.2.0")
        compileOnly("com.github.ben-manes.caffeine:caffeine:2.9.3")
        compileOnly("org.spongepowered:math:2.0.1")
        compileOnly(kotlin("stdlib"))
    }
    java {
        withSourcesJar()
    }
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjvm-default=all", "-Xextended-compiler-checks")
        }
    }
    <<<<<<< Updated upstream
    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    =======
    install("common")
    install("common-5")
    install("module-database")
    install("module-database-mongodb")
    install("module-configuration")
    install("module-kether")
    install("module-chat")
    install("module-lang")
    install("module-metrics")
    install("module-navigation")
    install("module-nms")
    install("module-nms-util")
    install("module-ui")
    install("platform-bukkit")
    install("expansion-command-helper")
    classifier = null
    version = "6.0.10-31"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms:Zaphkiel:1.7.0")
    compileOnly("public:ServerTours:1.6.5")
    compileOnly("public:ModelEngine:2.5.1")
    compileOnly("public:GermPlugin:4.0.3")
    compileOnly("net.citizensnpcs:Citizens:2.0.18")
    compileOnly("com.guillaumevdn.gcore:GCore:7.15.0")
    compileOnly("com.guillaumevdn.questcreator:QuestCreator:1")
    compileOnly("com.isnakebuzz.servernpc:ServerNPC:1")
    compileOnly("org.betonquest:betonquest:2.0.0")
    compileOnly("pl.betoncraft:betonquest:1.12.5")
    compileOnly("ink.ptms.core:v11903:11903:mapped")
    compileOnly("ink.ptms.core:v11903:11903:universal")
    compileOnly("ink.ptms.core:v11902:11902:mapped")
    compileOnly("ink.ptms.core:v11901:11901:mapped")
    compileOnly("ink.ptms.core:v11900:11900:mapped")
    compileOnly("ink.ptms.core:v11800:11800:mapped")
    compileOnly("ink.ptms.core:v11701:11701:mapped")
    compileOnly("ink.ptms.core:v11604:11604")
    compileOnly("ink.ptms.core:v11600:11600")
    compileOnly("ink.ptms.core:v11500:11500")
    compileOnly("ink.ptms.core:v11400:11400")
    compileOnly("ink.ptms.core:v11300:11300")
    compileOnly("ink.ptms.core:v11200:11200")
    compileOnly("ink.ptms.core:v11100:11100")
    compileOnly("ink.ptms.core:v11000:11000")
    compileOnly("ink.ptms.core:v10900:10900")
    compileOnly(kotlin("stdlib"))
    taboo(fileTree("libs"))
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.6.0")
    >>>>>>> Stashed changes
}

gradle.buildFinished {
    buildDir.deleteRecursively()
}

subprojects
    .filter { it.name != "project" && it.name != "plugin" }
    .forEach { proj ->
        proj.publishing { applyToSub(proj) }
    }

fun PublishingExtension.applyToSub(subProject: Project) {
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
            artifactId = subProject.name
            groupId = "ink.ptms.adyeshach"
            version = project.version.toString()
            artifact(subProject.tasks["kotlinSourcesJar"])
            artifact(subProject.tasks.jar)
            println("> Apply \"$groupId:$artifactId:$version\"")
        }
    }
}