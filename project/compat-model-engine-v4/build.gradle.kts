dependencies {
    compileOnly("com.ticxo:modelengine:4.0.8@jar")
    compileOnly(project(":project:common"))
    compileOnly(project(":project:common-impl"))
}

taboolib { subproject = true }