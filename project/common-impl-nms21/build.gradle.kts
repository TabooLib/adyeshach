configurations.all {
    dependencies.removeIf {
        it.name == "v11604"
    }
}

dependencies {
    compileOnly("org.joml:joml:1.10.2")
    compileOnly("ink.ptms.core:v12105:12105:mapped")
    compileOnly("ink.ptms.core:v12104:12104:mapped")
    compileOnly("io.netty:netty-all:4.1.86.Final")
    compileOnly("com.mojang:brigadier:1.0.500")
    compileOnly("net.md-5:bungeecord-chat:1.17")
    compileOnly(project(":project:common"))
    compileOnly(project(":project:common-impl"))
    compileOnly(project(":project:common-impl-nms"))
}

taboolib { subproject = true }