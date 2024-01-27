dependencies {
    compileOnly("public:ModelEngine:4.0.4")
    compileOnly(project(":project:common"))
    compileOnly(project(":project:common-impl"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}