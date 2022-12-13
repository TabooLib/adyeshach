dependencies {
    compileOnly("public:ServerTours:1.6.5")
    compileOnly("net.md-5:bungeecord-chat:1.17")
    compileOnly(project(":project:common"))

}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}