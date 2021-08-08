plugins {
    java
    id("io.izzel.taboolib") version "1.12"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
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
    classifier = null
    version = "6.0.0-pre32"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms:Zaphkiel:1.5.0")
    compileOnly("net.citizensnpcs:Citizens:2.0.18")
    compileOnly("com.guillaumevdn.gcore:GCore:7.15.0")
    compileOnly("com.guillaumevdn.questcreator:QuestCreator:5.30.0")
    compileOnly("com.isnakebuzz.servernpc:ServerNPC:1.12.10:RELEASE")
    compileOnly("ink.ptms.core:v11701:11701:mapped")
    compileOnly("ink.ptms.core:v11701:11701:universal")
    compileOnly("ink.ptms.core:v11604:11604:all")
    compileOnly("ink.ptms.core:v11600:11600:all")
    compileOnly("ink.ptms.core:v11500:11500:all")
    compileOnly("ink.ptms.core:v11400:11400:all")
    compileOnly("ink.ptms.core:v11300:11300:all")
    compileOnly("ink.ptms.core:v11200:11200:all")
    compileOnly("ink.ptms.core:v11100:11100:all")
    compileOnly("ink.ptms.core:v11000:11000:all")
    compileOnly("ink.ptms.core:v10900:10900:all")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}