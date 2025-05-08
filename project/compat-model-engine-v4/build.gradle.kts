dependencies {
    compileOnly(fileTree("libs"))
    compileOnly(project(":project:common"))
    compileOnly(project(":project:common-impl"))
}

taboolib { subproject = true }