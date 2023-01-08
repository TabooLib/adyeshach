dependencies {
    compileOnly("org.mongodb:mongo-java-driver:3.12.11")
    compileOnly("com.google.code.gson:gson:2.10")
    compileOnly(project(":project:common"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}