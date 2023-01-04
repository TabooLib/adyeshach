dependencies {
    compileOnly("ink.ptms.core:v11900:11900:mapped")
    compileOnly("ink.ptms.core:v11900:11900:universal")
    compileOnly("net.md-5:bungeecord-chat:1.17")
    compileOnly("io.netty:netty-all:4.1.77.Final")
    compileOnly("public:ServerTours:1.6.5")
    compileOnly("public:GermPlugin:4.0.3")
    compileOnly("public:ModelEngine:3.0.0")
    compileOnly(project(":project:common"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}