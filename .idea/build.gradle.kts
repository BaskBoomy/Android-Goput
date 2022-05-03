/*
  Copyright 2019 Kakao Corp.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URL

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.gradle}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

plugins {
    id("org.jetbrains.dokka") version "0.10.0"
    id("com.github.ben-manes.versions") version "0.27.0"
    `maven-publish`
}


val libraries = rootProject.subprojects.filter { project ->
    project.name !in Dokka.samples
}

val clean by tasks.registering(Delete::class) {
    delete(rootProject.buildDir)
}

val publishSdk by tasks.registering(Task::class) {
    libraries.filter { it.name !in Publish.excludedLibraries }.forEach { project ->
        dependsOn(project.tasks["publish"])
    }
    dependsOn(tasks["publishProjectPublicationToMavenRepository"])
}

val fullSourcePath = "${rootProject.buildDir}/full_source"

val copyProject by tasks.registering(Copy::class) {
    copy {
        from(rootProject.rootDir)
        into(fullSourcePath)
        exclude(
            relativePath(rootProject.buildDir),
            "**/.gradle",
            ".idea",
            ".project.info",
            "**/jacoco.exec",
            "**/*.iml",
            "buildSrc/build",
            "README.md",
            "rxpartnersdkv2Ko.md",
            "*.keystore",
            "Dockerfile",
            "Jenkinsfile",
            "deploy.sh"
        )
        exclude(
            rootProject.subprojects.map { relativePath(it.buildDir) }
        )
    }
}

val zipProject by tasks.registering(Zip::class) {
    dependsOn(copyProject)
    from(fullSourcePath)
    destinationDirectory.set(rootProject.buildDir)
    archiveBaseName.set(Publish.projectName)
    archiveVersion.set(SdkVersions.version)
}

val dokka by tasks.getting(DokkaTask::class) {
    outputDirectory = "${rootProject.buildDir}/dokka"
    subProjects = libraries.map { it.name }
    configuration {
        moduleName = Publish.projectName
        includes = Dokka.includes
        skipEmptyPackages = true
        listOf(
            "https://developer.android.com/reference/kotlin/",
            "http://reactivex.io/RxJava/javadoc/",
            "https://square.github.io/retrofit/2.x/retrofit/",
            "http://www.reactive-streams.org/reactive-streams-1.0.3-javadoc/"
//        "https://square.github.io/okhttp/4.x/okhttp/okhttp3/"
        ).forEach {
            externalDocumentationLink {
                // Root URL of the generated documentation to link with. The trailing slash is required!
                url = URL(it)
                // If package-list file located in non-standard location
                packageListUrl = URL("${it}package-list")
            }
        }
        listOf("androidx", "dagger", "io.reactivex", "com.google").forEach {
            perPackageOption {
                prefix = it
                suppress = true
            }
        }
    }
}

val zipDokka by tasks.registering(Zip::class) {
    dependsOn(dokka)
    from("${rootProject.buildDir}/dokka")
    destinationDirectory.set(rootProject.buildDir)
    archiveBaseName.set(Publish.dokkaArtifactId)
    archiveVersion.set(SdkVersions.version)
    delete("${rootProject.buildDir}/dokka")
}

// kts 에서 gradle.properties 에서 property 를 읽어보는 방식
val nexusSnapshotRepositoryUrl: String? by project
val nexusReleaseRepositoryUrl: String? by project
val nexusUsername: String? by project
val nexusPassword: String? by project

publishing {
    repositories {
        maven {
            url = if (SdkVersions.version.endsWith("-SNAPSHOT")) {
                nexusSnapshotRepositoryUrl?.let { uri(it) } ?: mavenLocal().url
            } else {
                nexusReleaseRepositoryUrl?.let { uri(it) } ?: mavenLocal().url
            }
            credentials {
                username = nexusUsername ?: ""
                password = nexusPassword ?: ""
            }
        }
    }
    publications {
        register("dokka", MavenPublication::class) {
            groupId = Publish.groupId
            artifactId = Publish.dokkaArtifactId
            version = SdkVersions.version
            artifact(zipDokka.get())
        }
        register("project", MavenPublication::class) {
            groupId = Publish.groupId
            artifactId = Publish.projectName
            version = SdkVersions.version
            artifact(zipProject.get())
        }
    }
}
