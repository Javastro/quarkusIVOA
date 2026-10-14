plugins {
    id("org.javastro.build.module.quarkus-lib")
    id("org.kordamp.gradle.jandex")  //necessary to make quarkus look for beans
}

dependencies {
    implementation(platform("io.quarkus.platform:quarkus-bom:3.40.1"))

    implementation("io.quarkus:quarkus-arc")
    implementation ("io.quarkus:quarkus-security")
}
scmVersion {
    tag {
        initialVersion({c,p -> "0.1.0"})
    }
}


tasks.withType<Javadoc> {
    dependsOn(tasks.named("jandex"))
}

