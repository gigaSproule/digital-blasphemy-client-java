import com.github.spotbugs.snom.SpotBugsTask

plugins {
    `java-library`
    jacoco
    checkstyle
    id("com.vanniktech.maven.publish") version "0.34.0"
    id("com.github.spotbugs") version "6.2.2"
    id("info.solidsoft.pitest") version "1.19.0-rc.1"
}

repositories {
    mavenCentral()
}

group = "com.benjaminsproule"
version = rootProject.file("version.txt").readText().trim()

dependencies {
    compileOnly(libs.spotbugs.annotations)
    implementation(libs.okhttp)
    implementation(libs.jackson)
    testCompileOnly(libs.spotbugs.annotations)
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
                minimum = "1.00".toBigDecimal()
            }
        }
        rule {
            limit {
                counter = "LINE"
                minimum = "1.00".toBigDecimal()
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
                minimum = "1.00".toBigDecimal()
            }
        }
        rule {
            limit {
                counter = "METHOD"
                minimum = "1.00".toBigDecimal()
            }
        }
        rule {
            limit {
                counter = "CLASS"
                minimum = "1.00".toBigDecimal()
            }
        }
    }
}

checkstyle {
    config =
        project.resources.text.fromUri("https://raw.githubusercontent.com/gigaSproule/checkstyle-config/refs/heads/main/checkstyle.xml")
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

pitest {
    junit5PluginVersion = "1.2.3"
    mutationThreshold = 100
    coverageThreshold = 100
}

tasks.check {
    finalizedBy(tasks.jacocoTestCoverageVerification, tasks.pitest)
}

mavenPublishing {
    coordinates("${project.group}", project.name, project.version.toString())
    publishToMavenCentral()
    signAllPublications()
    pom {
        name = "Digital Blasphemy Client"
        description = "A client to interact with the Digital Blasphemy API"
        inceptionYear = "2025"
        url = "https://github.com/gigaSproule/digital-blasphemy-client-java/"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        scm {
            connection = "scm:git:git://github.com/gigaSproule/digital-blasphemy-client-java.git"
            developerConnection = "scm:git:ssh://github.com/gigaSproule/digital-blasphemy-client-java.git"
            url = "https://github.com/gigaSproule/digital-blasphemy-client-java/"
        }
    }
}
