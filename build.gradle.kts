import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "7.0.0"
  id("org.liquibase.gradle") version "2.0.4"
}

group = "com.programm"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.3.0"
val junitJupiterVersion = "5.8.2"

val mainVerticleName = "com.programm.vertx.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  liquibaseRuntime("org.liquibase:liquibase-core:4.10.0")
  liquibaseRuntime("org.liquibase:liquibase-groovy-dsl:3.0.2")
  liquibaseRuntime("org.postgresql:postgresql:42.3.4")

  implementation("com.fasterxml.jackson.core:jackson-databind:2.13.3")
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-core")
  implementation("io.vertx:vertx-web:$vertxVersion")
  implementation("io.vertx:vertx-config:$vertxVersion")
  implementation("io.vertx:vertx-pg-client:$vertxVersion")
  implementation("org.hibernate.reactive:hibernate-reactive-core:1.1.6.Final")
  implementation("org.apache.logging.log4j:log4j-core:2.17.2")
  implementation("am.ik.yavi:yavi:0.11.2")

  runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.77.Final:osx-x86_64")

  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
  testImplementation("org.mockito:mockito-core:4.5.1")
  testImplementation("org.mockito:mockito-junit-jupiter:4.6.0")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

liquibase {
  activities.register("main") {
    val dbUrl by project.extra.properties
    val dbUser by project.extra.properties
    val dbPassword by project.extra.properties

    this.arguments = mapOf(
            "logLevel" to "info",
            "changeLogFile" to "src/main/resources/db/changelog.sql",
            "url" to dbUrl,
            "username" to dbUser,
            "password" to dbPassword,
            "driver" to "org.postgresql.Driver"
    )
  }
  runList = "main"
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
