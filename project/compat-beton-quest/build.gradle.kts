dependencies {
    compileOnly("org.betonquest:betonquest:2.0.0")
    compileOnly("pl.betoncraft:betonquest:1.12.5")
    compileOnly(project(":project:common"))
    compileOnly(project(":project:common-impl"))
}

taboolib { subproject = true }