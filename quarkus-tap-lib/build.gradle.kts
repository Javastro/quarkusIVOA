plugins {
    id("org.javastro.build.module.quarkus-lib")
}

dependencies {
    implementation(platform("org.javastro:bom:2026.2"))
    api("org.javastro.ivoa.core:tap:0.1.0-SNAPSHOT")
    implementation("org.javastro.ivoa.core:common:0.1.0-SNAPSHOT")
    implementation("io.quarkus:quarkus-rest")
    implementation("org.jspecify:jspecify:1.0.0")

}

version = "1.0.0-SNAPSHOT"

