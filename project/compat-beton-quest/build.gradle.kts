dependencies {
    compileOnly("org.betonquest:betonquest:2.0.0")
    compileOnly("pl.betoncraft:betonquest:1.12.5")
    compileOnly(project(":project:common"))
    compileOnly(project(":project:common-impl"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}