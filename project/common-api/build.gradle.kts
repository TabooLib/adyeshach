val taboolib_version: String by project

plugins {
    id("io.izzel.taboolib") version "1.50"
}

taboolib {
    install("common", "common-5", "module-nms", "module-nms-util", "module-navigation", "module-lang", "module-configuration", "platform-bukkit")
    options("skip-plugin-file", "skip-minimize", "keep-kotlin-module")
    classifier = null
    version = taboolib_version
}

dependencies {
    compileOnly("public:ServerTours:1.6.5")
    compileOnly(project(":project:common"))
    compileOnly("net.md-5:bungeecord-chat:1.17")
    compileOnly("com.eatthepath:fast-uuid:0.2.0")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}