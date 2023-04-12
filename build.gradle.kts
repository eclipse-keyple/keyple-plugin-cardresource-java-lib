// /////////////////////////////////////////////////////////////////////////////
//  GRADLE CONFIGURATION
// /////////////////////////////////////////////////////////////////////////////

plugins {
  java
  id("com.diffplug.spotless") version "6.18.0"
  id("org.sonarqube") version "4.0.0.2929"
  jacoco
}

buildscript {
  repositories {
    mavenLocal()
    maven(url = "https://repo.eclipse.org/service/local/repositories/maven_central/content")
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
  maven(url = "https://repo.eclipse.org/service/local/repositories/maven_central/content")
  mavenCentral()
  maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
  implementation("org.calypsonet.terminal:calypsonet-terminal-reader-java-api:1.2.+") {
    isChanging = true
  }
  implementation("org.eclipse.keyple:keyple-common-java-api:2.0.0")
  implementation("org.eclipse.keyple:keyple-plugin-java-api:2.1.0-SNAPSHOT") { isChanging = true }
  implementation("org.eclipse.keyple:keyple-service-java-lib:2.2.0-SNAPSHOT") { isChanging = true }
  implementation("org.eclipse.keyple:keyple-service-resource-java-lib:2.0.2")
  implementation("org.eclipse.keyple:keyple-util-java-lib:2.3.0")
  implementation("org.slf4j:slf4j-api:2.0.5")
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
    kotlinGradle {
      target("build.gradle.kts") // default target for kotlinGradle
      ktfmt()
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