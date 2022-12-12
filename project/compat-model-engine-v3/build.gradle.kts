val taboolib_version: String by project

plugins {
    id("io.izzel.taboolib") version "1.50"
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
    compileOnly(project(":project:common-impl"))
    compileOnly("public:ModelEngine:3.0.0")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}