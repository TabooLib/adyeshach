val taboolib_version: String by project

plugins {
    id("io.izzel.taboolib") version "1.40"
}

taboolib {
    install("common", "common-5", "module-configuration")
    options("skip-minimize", "keep-kotlin-module")
    classifier = null
    version = taboolib_version
}

dependencies {
    compileOnly(project(":project:common"))
    compileOnly(project(":project:common-api"))
    compileOnly(project(":project:common-default-impl"))
    compileOnly("public:ModelEngine:2.5.1")
}