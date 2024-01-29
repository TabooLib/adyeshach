dependencies {
    compileOnly("ink.ptms.core:v12004:12004:mapped")
    compileOnly("net.citizensnpcs:Citizens:2.0.18")
    compileOnly(project(":project:common"))
    compileOnly(project(":project:common-impl"))
}

taboolib { subproject = true }