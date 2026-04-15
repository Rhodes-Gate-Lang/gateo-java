import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    `java-library`
    alias(libs.plugins.protobuf)
    `maven-publish`
}

group = "com.rhodesgatelang"
version = providers.gradleProperty("gateoJavaVersion").getOrElse("2.0.1-SNAPSHOT")

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    api(libs.protobuf.java)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

val gateoProtobufVersion = libs.versions.gateoProtobuf.get().toString()

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$gateoProtobufVersion"
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Xlint:deprecation")
}

tasks.withType<Javadoc>().configureEach {
    val main = sourceSets["main"]
    setSource(fileTree("src/main/java"))
    classpath = main.compileClasspath + main.output
    (options as StandardJavadocDocletOptions).encoding = "UTF-8"
    (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    if (JavaVersion.current().isJava9Compatible) {
      (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = "gateo-java"
            version = project.version.toString()
            pom {
                name.set("gateo-java")
                description.set("Java library for reading and writing .gateo gate objects (protobuf wire format gateo.v2).")
                url.set("https://github.com/Rhodes-Gate-Lang/gateo-java")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Rhodes-Gate-Lang/gateo-java.git")
                    developerConnection.set("scm:git:ssh://github.com:Rhodes-Gate-Lang/gateo-java.git")
                    url.set("https://github.com/Rhodes-Gate-Lang/gateo-java")
                }
            }
        }
    }
}
