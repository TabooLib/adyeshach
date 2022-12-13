dependencies {
    compileOnly("net.md-5:bungeecord-chat:1.17")
    compileOnly("ink.ptms.core:v11900:11900:universal")
    compileOnly(project(":project:common"))
    compileOnly(project(":project:common-api"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}