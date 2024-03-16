dependencies {
    compileOnly("paper:v12004:12004:core")
    compileOnly("paper:v12004:12004:api")
    compileOnly(project(":project:common-impl-nms"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

taboolib { subproject = true }