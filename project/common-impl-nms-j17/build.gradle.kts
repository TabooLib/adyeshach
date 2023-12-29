dependencies {
    compileOnly("org.joml:joml:1.10.2")
    compileOnly("ink.ptms.core:v12004:12004:mapped")
    compileOnly("ink.ptms.core:v12000:12000:mapped")
    compileOnly("ink.ptms.core:v11904:11904:mapped")
    compileOnly("ink.ptms.core:v11904:11904:universal")
    compileOnly("ink.ptms.core:v11903:11903:mapped")
    compileOnly("ink.ptms.core:v11903:11903:universal")
    compileOnly("net.md-5:bungeecord-chat:1.17")
    compileOnly(project(":project:common"))
    compileOnly(project(":project:common-impl-nms"))
    compileOnly(project(":project:api-data-serializer"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}