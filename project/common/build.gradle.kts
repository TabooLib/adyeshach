dependencies {
    compileOnly("ink.ptms.core:v11900:11900:universal")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}