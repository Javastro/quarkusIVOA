plugins {
    id("org.javastro.build.module.quarkus-lib")
}

dependencies {
    api("org.javastro.ivoa.core:tap:0.9.4")
    implementation(project(":quarkus-config"))
    implementation("org.javastro.ivoa.core:common:0.9.0")
    implementation("io.quarkus:quarkus-rest")
    implementation("org.jspecify:jspecify:1.0.0")

}

