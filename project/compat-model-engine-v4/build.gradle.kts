dependencies {
    compileOnly("public:ModelEngine:4.0.4")
    compileOnly(project(":project:common"))
    compileOnly(project(":project:common-impl"))
}

taboolib { subproject = true }