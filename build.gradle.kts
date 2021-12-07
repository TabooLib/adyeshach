plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.31"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
    id("org.jetbrains.dokka") version "1.5.30"
}

taboolib {
    description {
        contributors {
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
    version = "6.0.6-22"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms:Zaphkiel:1.7.0")
    compileOnly("public:ServerTours:1.6.5")
    compileOnly("public:ModelEngine:2.2.0")
    compileOnly("public:GermPlugin:4.0.3")
    compileOnly("net.citizensnpcs:Citizens:2.0.18")
    compileOnly("com.guillaumevdn.gcore:GCore:7.15.0")
    compileOnly("com.guillaumevdn.questcreator:QuestCreator:5.30.0")
    compileOnly("com.isnakebuzz.servernpc:ServerNPC:1.12.10:RELEASE")
    compileOnly("org.betonquest:betonquest:2.0.0")
    compileOnly("pl.betoncraft:betonquest:1.12.5")
    compileOnly("ink.ptms.core:v11701:11701-minimize:mapped")
    compileOnly("ink.ptms.core:v11701:11701-minimize:universal")
    compileOnly("ink.ptms.core:v11604:11604:all")
    compileOnly("ink.ptms.core:v11600:11600-minimize")
    compileOnly("ink.ptms.core:v11500:11500:all")
    compileOnly("ink.ptms.core:v11400:11400-minimize")
    compileOnly("ink.ptms.core:v11300:11300:all")
    compileOnly("ink.ptms.core:v11200:11200-minimize")
    compileOnly("ink.ptms.core:v11100:11100:all")
    compileOnly("ink.ptms.core:v11000:11000:all")
    compileOnly("ink.ptms.core:v10900:10900:all")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.5.30")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    repositories {
        maven {
            url = uri("https://repo2s.ptms.ink/repository/maven-releases/")
            credentials {
                username = project.findProperty("user").toString()
                password = project.findProperty("password").toString()
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            groupId = "ink.ptms"
        }
    }
}

//tasks.withType<org.jetbrains.dokka.gradle.DokkaTask> {
//    outputFormat = "javadoc"
//    outputDirectory = "$buildDir/dokka"
//    configuration {
//        skipDeprecated = true
//        reportUndocumented = true
//        skipEmptyPackages = true
//        noJdkLink = true
//        noStdlibLink = true
//    }
//}

