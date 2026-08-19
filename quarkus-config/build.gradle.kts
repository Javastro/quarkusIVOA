plugins {
    id("org.javastro.build.module.quarkus-lib")
    id("org.kordamp.gradle.jandex")  //necessary to make quarkus look for beans
}

dependencies {
    implementation("io.smallrye.config:smallrye-config")
}

tasks.withType<Javadoc> {
    dependsOn(tasks.named("jandex"))
}

