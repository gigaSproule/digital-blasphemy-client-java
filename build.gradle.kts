plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

group = "com.benjaminsproule"
version = "0.1.0"

dependencies {
    implementation(libs.okhttp)
    implementation(libs.jackson)
    testImplementation(libs.assertj)
    testImplementation(libs.wiremock)
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter("5.12.1")
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
