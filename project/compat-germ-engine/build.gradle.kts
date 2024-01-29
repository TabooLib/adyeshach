dependencies {
    compileOnly("public:GermPlugin:4.0.3")
    compileOnly(project(":project:common"))
    compileOnly(project(":project:common-impl"))
}

taboolib { subproject = true }