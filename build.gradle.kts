import com.github.spotbugs.snom.SpotBugsTask

plugins {
    `java-library`
    jacoco
    checkstyle
    `maven-publish`
    id("com.github.spotbugs") version "6.2.2"
}

repositories {
    mavenCentral()
}

group = "com.benjaminsproule"
version = "0.1.0"

dependencies {
    implementation(libs.okhttp)
    implementation(libs.jackson)
    compileOnly(libs.spotbugs.annotations)
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

spotbugs {
    excludeFilter = file("config/spotbugs/exclude.xml")
}

tasks.withType<SpotBugsTask> {
    reports {
        create("html") {
            enabled = true
            required = true
        }
    }
}

tasks.check {
    finalizedBy(tasks.jacocoTestCoverageVerification)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])
            pom {
                name = "Digital Blasphemy Client"
                description = "A client to interact with the Digital Blasphemy API"
                url = "https://github.com/gigaSproule/digital-blasphemy-client-java/"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/gigaSproule/digital-blasphemy-client-java.git"
                    developerConnection = "scm:git:ssh://github.com/gigaSproule/digital-blasphemy-client-java.git"
                    url = "https://github.com/gigaSproule/digital-blasphemy-client-java/"
                }
            }
        }
    }
}
