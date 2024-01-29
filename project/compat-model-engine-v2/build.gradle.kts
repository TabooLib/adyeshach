dependencies {
    compileOnly("public:ModelEngine:2.5.1")
    compileOnly(project(":project:common"))
    compileOnly(project(":project:common-impl"))
}

taboolib { subproject = true }