// /////////////////////////////////////////////////////////////////////////////
//  GRADLE CONFIGURATION
// /////////////////////////////////////////////////////////////////////////////

plugins {
    java
    id("com.diffplug.spotless") version "6.25.0"
    id("org.sonarqube") version "3.1"
    jacoco
}

buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    dependencies { classpath("org.eclipse.keyple:keyple-gradle:0.2.+") { isChanging = true } }
}

apply(plugin = "org.eclipse.keyple")

// /////////////////////////////////////////////////////////////////////////////
//  APP CONFIGURATION
// /////////////////////////////////////////////////////////////////////////////
repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation("org.eclipse.keypop:keypop-reader-java-api:2.0.1-SNAPSHOT") {isChanging=true}
    implementation("org.eclipse.keyple:keyple-common-java-api:2.0.1-SNAPSHOT") {isChanging=true}
    implementation("org.eclipse.keyple:keyple-plugin-java-api:2.3.1-SNAPSHOT") {isChanging=true}
    implementation("org.eclipse.keyple:keyple-service-resource-java-lib:3.0.1-SNAPSHOT") {isChanging=true}
    implementation("org.eclipse.keyple:keyple-util-java-lib:2.3.2-SNAPSHOT") {isChanging=true}
    implementation("org.slf4j:slf4j-api:1.7.32")
    testImplementation("org.slf4j:slf4j-simple:1.7.32")
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.vintage:junit-vintage-engine")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("org.mockito:mockito-core:5.11.0")
}

val javaSourceLevel: String by project
val javaTargetLevel: String by project

java {
    sourceCompatibility = JavaVersion.toVersion(javaSourceLevel)
    targetCompatibility = JavaVersion.toVersion(javaTargetLevel)
    println("Compiling Java $sourceCompatibility to Java $targetCompatibility.")
    withJavadocJar()
    withSourcesJar()
}

// /////////////////////////////////////////////////////////////////////////////
//  TASKS CONFIGURATION
// /////////////////////////////////////////////////////////////////////////////
tasks {
    spotless {
        java {
            target("src/**/*.java")
            licenseHeaderFile("${project.rootDir}/LICENSE_HEADER")
            importOrder("java", "javax", "org", "com", "")
            removeUnusedImports()
            googleJavaFormat()
        }
    }
    test {
        testLogging { events("passed", "skipped", "failed") }
        finalizedBy("jacocoTestReport")
    }
    jacocoTestReport {
        dependsOn("test")
        reports {
            xml.isEnabled = true
            csv.isEnabled = false
            html.isEnabled = true
        }
    }
    sonarqube {
        properties {
            property("sonar.projectKey", "eclipse_" + project.name)
            property("sonar.organization", "eclipse")
            property("sonar.host.url", "https://sonarcloud.io")
            property("sonar.login", System.getenv("SONAR_LOGIN"))
            System.getenv("BRANCH_NAME")?.let {
                if (!"main".equals(it)) {
                    property("sonar.branch.name", it)
                }
            }
        }
    }
}