val taboolib_version: String by project

plugins {
    id("io.izzel.taboolib") version "1.40"
}

taboolib {
    description {
        name(rootProject.name)
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
    install("common", "platform-bukkit")
    options("skip-minimize", "keep-kotlin-module")
    classifier = null
    version = taboolib_version
}

dependencies {
    api(project(":project:common"))
}