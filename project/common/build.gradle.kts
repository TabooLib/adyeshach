val taboolib_version: String by project

plugins {
    id("io.izzel.taboolib") version "1.40"
}

taboolib {
    install("common", "common-5", "module-configuration", "module-nms")
    options("skip-minimize", "keep-kotlin-module")
    classifier = null
    version = taboolib_version
    relocate("com.eatthepath.uuid", "ink.ptms.adyeshach.taboolib.library.uuid")
}

dependencies {
    taboo("com.eatthepath:fast-uuid:0.2.0")
}