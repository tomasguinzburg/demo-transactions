/*
 * This file was generated by the Gradle "init" task.
 *
 * This is a general purpose Gradle build.
 * Learn more about Gradle by exploring our samples at https://docs.gradle.org/7.3.3/samples
 */
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    idea
    application
    jacoco
//    id("com.heroku.sdk.heroku-gradle") version "2.0.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.freefair.lombok") version "6.3.0"

}

group "com.tomasguinzburg.demo.transactions"
version "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("com.google.dagger:dagger-compiler:2.40.5")
    annotationProcessor("org.projectlombok:lombok:1.18.22")
    compileOnly("org.projectlombok:lombok:1.18.22")
    implementation("com.google.dagger:dagger:2.40.5")
    implementation("com.sparkjava:spark-core:2.9.3")
    implementation("org.slf4j:slf4j-simple:1.7.35")
    implementation("junit:junit:4.13.2")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.google.guava:guava:31.0.1-jre")                     //For more collectors
    testAnnotationProcessor("com.google.dagger:dagger-compiler:2.40.5")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.22")
    testCompileOnly("org.projectlombok:lombok:1.18.22")
    testImplementation("org.mockito:mockito-core:4.3.1")
    testImplementation("com.google.dagger:dagger:2.40.5")
    testImplementation("com.sparkjava:spark-core:2.9.3")
    testImplementation("org.slf4j:slf4j-simple:1.7.35")
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.code.gson:gson:2.8.9")
}


java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withSourcesJar()
}

application() {
    mainClass.set("com.tomasguinzburg.demo.impl.application.App")
    applicationDefaultJvmArgs = mutableListOf("-XX:+UseContainerSupport")
}

jacoco {
    toolVersion = "0.8.7"
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacoco"))
    }
}

tasks.test {
    useJUnit()
    maxHeapSize = "1G"
    finalizedBy(tasks.jacocoTestCoverageVerification) // report is always generated after tests run
}

//heroku {
//    appName = "guinzburg-demo-transactions"
//    includes = mutableListOf("build/libs/demo-transactions-all.jar")
//    setIncludeBuildDir(false)
//    jdkVersion = "11"
//}

//Set code coverage threshold to 70%, but exclude all auto-generated sources, router mappings and dependency injection configuration
tasks.withType<JacocoCoverageVerification> {
    violationRules {
        rule {
            limit {
                minimum = "0.7".toBigDecimal()
            }
        }

        rule {
            isEnabled = false
            element = "CLASS"
            includes = listOf("org.gradle.*")

            limit {
                counter = "LINE"
                value = "TOTALCOUNT"
                maximum = "0.7".toBigDecimal()
            }
        }
    }

    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.map {
            fileTree(it).apply {
                exclude("com/generate/**", "com/tomasguinzburg/demo/impl/rest/Router.class", "com/tomasguinzburg/demo/impl/application/**")
            }
        }))
    }
}

tasks.withType<JacocoReport> {
    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.map {
            fileTree(it).apply {
                exclude("com/generate/**", "com/tomasguinzburg/demo/impl/rest/Router.class", "com/tomasguinzburg/demo/impl/application/**")
            }
        }))
    }
}

tasks.register("stage"){
    dependsOn(tasks.clean, tasks.installDist)
}