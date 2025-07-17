plugins {
    `java-library`
    jacoco
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

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                counter = "INSTRUCTION"
                minimum = "0.98".toBigDecimal()
            }
        }
        rule {
            limit {
                counter = "LINE"
                minimum = "0.96".toBigDecimal()
            }
        }
        rule {
            limit {
                counter = "BRANCH"
                minimum = "1.00".toBigDecimal()
            }
        }
        rule {
            limit {
                counter = "COMPLEXITY"
                minimum = "0.93".toBigDecimal()
            }
        }
        rule {
            limit {
                counter = "METHOD"
                minimum = "0.92".toBigDecimal()
            }
        }
        rule {
            limit {
                counter = "CLASS"
                minimum = "0.95".toBigDecimal()
            }
        }
    }
}

tasks.check {
    finalizedBy(tasks.jacocoTestCoverageVerification)
}
