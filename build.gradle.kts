plugins {
    id("org.javastro.build.feature.lifecycle.root-monorepo")
}

subprojects {
    beforeEvaluate {
        group = "org.javastro.ivoa.core.quarkus"
    }
}

