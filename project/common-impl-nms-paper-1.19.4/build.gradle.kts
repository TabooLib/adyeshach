dependencies {
    compileOnly("ink.ptms.core:v11904:11904.3")
    compileOnly(project(":project:common-impl-nms"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

taboolib { subproject = true }