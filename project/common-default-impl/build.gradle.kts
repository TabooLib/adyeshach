val taboolib_version: String by project

plugins {
    id("io.izzel.taboolib") version "1.40"
}

taboolib {
    install("common", "common-5", "module-chat", "module-lang", "module-configuration", "module-nms", "module-nms-util", "platform-bukkit")
    options("skip-plugin-file", "skip-minimize", "keep-kotlin-module")
    classifier = null
    version = taboolib_version
}

dependencies {
    compileOnly("net.md-5:bungeecord-chat:1.17")
    compileOnly(project(":project:common"))
    compileOnly(project(":project:common-api"))
    compileOnly(project(":project:module-editor"))
    compileOnly("io.netty:netty-all:4.1.77.Final")
    compileOnly("com.eatthepath:fast-uuid:0.2.0")
    compileOnly("com.github.ben-manes.caffeine:caffeine:2.8.5")
}