plugins {
    id("org.javastro.build.feature.lifecycle.root")
}

subprojects {
    beforeEvaluate {
        group = "org.javastro.ivoa.core.quarkus"
    }
}