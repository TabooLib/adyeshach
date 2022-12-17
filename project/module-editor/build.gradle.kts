dependencies {
    compileOnly(project(":project:common"))
    compileOnly(project(":project:common-impl"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}