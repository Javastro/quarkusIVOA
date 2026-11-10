plugins {
    id("org.javastro.build.module.quarkus-lib")
}

dependencies {
    api("org.javastro.ivoa.core:uws:0.9.2")
    implementation("io.quarkus:quarkus-narayana-jta")


}

