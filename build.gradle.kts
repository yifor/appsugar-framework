import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

buildscript {
    val repos by extra { listOf("http://maven.aliyun.com/nexus/content/groups/public", "https://jcenter.bintray.com/") }
    extra["kotlin.version"] = "1.3.50"
    repositories {
        for (u in repos) {
            maven(u)
        }
    }
}

plugins {
    val kotlinVersion = "1.3.50"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion apply false
    id("net.researchgate.release") version "2.8.1"
    id("org.springframework.boot") version "2.1.8.RELEASE" apply false
    idea
    `maven-publish`
}

val repos: List<String> by extra

configure(allprojects) {
    apply {
        plugin("io.spring.dependency-management")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("java")
        plugin("maven-publish")
        plugin("idea")
    }
    repositories { for (u in repos) { maven(u) } }
    dependencies {
        testCompile("ch.qos.logback:logback-classic:1.2.3")
        testCompile("org.junit.jupiter:junit-jupiter-api:5.1.0")
        testRuntime("org.junit.jupiter:junit-jupiter-engine:5.1.0")
    }
    idea {
        module {
            inheritOutputDirs = false
            outputDir = file("$buildDir/classes/kotlin/main/")
            testOutputDir = file("$buildDir/classes/kotlin/test/")
        }
    }
    val sourcesJar by tasks.creating(Jar::class) {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        description = "Assembles sources JAR"
        archiveClassifier.set("sources")
        from(project.sourceSets.getByName("main").allSource)
    }
    val mavenUser: String by extra
    val mavenPassword: String by extra
    publishing {
        repositories {
            val endWith = if ((version as String).endsWith("SNAPSHOT")) "maven-snapshots/" else "maven-releases/"
            maven("https://repository.bj1580.top/repository/$endWith") {
                credentials {
                    username = mavenUser
                    password = mavenPassword
                }
            }
        }
        publications {
            create<MavenPublication>("mavenJava") {
                from(components.getByName("java"))
                artifact(sourcesJar)
            }
        }
    }
    tasks {
        "test"(Test::class) {
            failFast = true
            useJUnitPlatform()
            systemProperties["refreshDb"] = true
            systemProperties["spring.jpa.hibernate.ddl-auto"] = "create-drop"
        }
        Unit
    }
    tasks.withType<JavaCompile> { options.encoding = "UTF-8" }
    tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }
    rootProject.tasks.findByName("afterReleaseBuild")?.apply { dependsOn("publish") }
}

project(":appsugar-framework-netty"){
    dependencies{
        implementation("io.netty:netty-all:4.1.39.Final")
    }
}