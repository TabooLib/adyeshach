val taboolib_version: String by project

plugins {
    id("io.izzel.taboolib") version "1.40"
}

taboolib {
    install("common", "common-5", "module-configuration", "platform-bukkit")
    options("skip-plugin-file", "skip-minimize", "keep-kotlin-module")
    classifier = null
    version = taboolib_version
}

dependencies {
    compileOnly(project(":project:common"))
    compileOnly(project(":project:common-api"))
    compileOnly(project(":project:module-editor"))
    compileOnly("io.netty:netty-all:4.1.77.Final")
}