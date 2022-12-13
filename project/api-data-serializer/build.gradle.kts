dependencies {
    compileOnly("ink.ptms.core:v10900:10900")
    compileOnly("ink.ptms.core:v11903:11903:mapped")
    compileOnly("ink.ptms.core:v11903:11903:universal")
    compileOnly(project(":project:common"))
    compileOnly(project(":project:common-api"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}