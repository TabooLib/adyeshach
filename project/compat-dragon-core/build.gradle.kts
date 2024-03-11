dependencies {
    compileOnly("public:DragonCore:2.6.2.9")
    compileOnly(project(":project:common"))
    compileOnly(project(":project:common-impl"))
}

taboolib { subproject = true }
