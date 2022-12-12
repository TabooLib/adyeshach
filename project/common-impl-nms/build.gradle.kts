val taboolib_version: String by project

plugins {
    id("io.izzel.taboolib") version "1.50"
}

taboolib {
    install("common", "common-5", "module-chat", "module-lang", "module-configuration", "module-nms", "module-nms-util", "platform-bukkit")
    options("skip-plugin-file", "skip-minimize", "keep-kotlin-module")
    classifier = null
    version = taboolib_version
}

dependencies {
    compileOnly("ink.ptms.core:v11900:11900:mapped")
    compileOnly("ink.ptms.core:v11900:11900:universal")
    compileOnly("ink.ptms.core:v11903:11903:mapped")
    compileOnly("ink.ptms.core:v11800:11800:mapped")
    compileOnly("net.md-5:bungeecord-chat:1.17")
    compileOnly(project(":project:common"))
    compileOnly(project(":project:common-api"))
    compileOnly(project(":project:common-impl-nms-j17"))
    compileOnly(project(":project:api-data-serializer"))
    compileOnly("com.github.ben-manes.caffeine:caffeine:2.8.5")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}