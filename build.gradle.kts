import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  jacoco
  id("io.freefair.lombok") version "6.5.0.3"
  id("com.github.johnrengelman.shadow") version "7.0.0"
  id("org.liquibase.gradle") version "2.0.4"
}

group = "com.programm"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.3.1"
val junitJupiterVersion = "5.8.2"
val mutinyVertXVersion = "2.24.0"

val mainVerticleName = "com.programm.vertx.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

val jooqVersion = "3.16.6"
val jooqRxVersion = "6.5.4"
val jacksonDatabindVersion = "2.13.3"
val postgresqlVersion = "42.3.6"
val slf4jApiVersion = "1.7.36"
val logbackVersion = "1.2.11"
val logbackContribVersion = "0.1.5"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  liquibaseRuntime("org.liquibase:liquibase-core:4.11.0")
  liquibaseRuntime("org.liquibase:liquibase-groovy-dsl:3.0.2")
  liquibaseRuntime("org.postgresql:postgresql:$postgresqlVersion")

  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-core")
  implementation("io.vertx:vertx-web:$vertxVersion")
  implementation("io.vertx:vertx-config:$vertxVersion")
  implementation("io.vertx:vertx-pg-client:$vertxVersion")
  implementation("io.vertx:vertx-auth-jwt:$vertxVersion")
  implementation("io.smallrye.reactive:smallrye-mutiny-vertx-core:$mutinyVertXVersion")
  implementation("io.smallrye.reactive:smallrye-mutiny-vertx-web:$mutinyVertXVersion")
  implementation("io.smallrye.reactive:smallrye-mutiny-vertx-pg-client:$mutinyVertXVersion")
  implementation("io.smallrye.reactive:smallrye-mutiny-vertx-sql-client:$mutinyVertXVersion")
  implementation("io.smallrye.reactive:smallrye-mutiny-vertx-auth-jwt:$mutinyVertXVersion")
  implementation("org.slf4j:slf4j-api:$slf4jApiVersion")
  implementation("ch.qos.logback:logback-core:$logbackVersion")
  implementation("ch.qos.logback:logback-classic:$logbackVersion")
  implementation("ch.qos.logback.contrib:logback-json-classic:$logbackContribVersion")
  implementation("ch.qos.logback.contrib:logback-jackson:$logbackContribVersion")
  implementation("am.ik.yavi:yavi:0.11.3")
  implementation("com.fasterxml.jackson.core:jackson-databind:${jacksonDatabindVersion}")

  runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.77.Final:osx-x86_64")

  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
  testImplementation("org.mockito:mockito-core:4.6.1")
  testImplementation("org.mockito:mockito-junit-jupiter:4.6.1")
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

val dbUrl: String by project.extra.properties
val dbUser: String by project.extra.properties
val dbPassword: String by project.extra.properties
val dbDriver: String by project.extra.properties

liquibase {
  activities.register("main") {
    this.arguments = mapOf(
      "logLevel" to "info",
      "changeLogFile" to "src/main/resources/db/changelog.xml",
      "url" to dbUrl,
      "username" to dbUser,
      "password" to dbPassword,
      "driver" to "org.postgresql.Driver"
    )
  }
  runList = "main"
}

//tasks.withType<Test> {
//  configure<JacocoTaskExtension> {
//    isIncludeNoLocationClasses = true
//  }
//}

tasks.test {
  finalizedBy(":jacocoTestReport")
}
//
//jacoco {
//  toolVersion = "0.8.6"
//  //reportsDirectory = file("$buildDir/report/")
//}
tasks.jacocoTestReport {
  dependsOn(":test")

  reports {
    xml.required.set(false)
    csv.required.set(true)

    html.outputLocation.set(file("${buildDir}/reports/jacoco/Html"))
    csv.outputLocation.set(file("${buildDir}/reports/jacoco/jacoco.csv"))
  }
}
//////Test coverage Rule to make sure code coverage is 100 %
//
tasks.jacocoTestCoverageVerification {
  violationRules {
    rule {
      element = "CLASS"
      limit {
        counter = "LINE"
        value = "COVEREDRATIO"
        minimum = 0.5.toBigDecimal()
      }
//      excludes = [
//        'com.cicd.herokuautodeploy.model.*',
//      ]
    }
  }
}


tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}
