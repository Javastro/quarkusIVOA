plugins {
    id("org.javastro.build.module.quarkus-lib")
    id("org.kordamp.gradle.jandex")  //necessary to make quarkus look for beans
}

dependencies {
    implementation("io.smallrye.config:smallrye-config")
}
scmVersion {
    tag {
        initialVersion({c,p -> "0.1.0"})
    }
}


tasks.withType<Javadoc> {
    dependsOn(tasks.named("jandex"))
}

