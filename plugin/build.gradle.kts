taboolib {
    description {
        name(rootProject.name)
        contributors {
            name("IzzelAliz")
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
    // asm
    relocate("org.objectweb.asm.", "org.objectweb.asm9.")
    // include
    relocate("com.eatthepath.uuid.", "ink.ptms.adyeshach.taboolib.library.uuid.")
    relocate("org.spongepowered.math.", "ink.ptms.adyeshach.taboolib.library.math.")
    // download
    relocate("org.mongodb.", "org.mongodb_3_12_11.")
    relocate("com.github.benmanes.caffeine.", "com.github.benmanes.caffeine_2_9_3.")
}

dependencies {
    taboo("com.eatthepath:fast-uuid:0.2.0")
    taboo("org.spongepowered:math:2.0.1")
}

tasks {
    jar {
        // 构件名
        archiveFileName.set("${rootProject.name}-${archiveFileName.get().substringAfter('-')}")
        // 打包子项目源代码
        rootProject.subprojects.forEach { from(it.sourceSets["main"].output) }
    }
}