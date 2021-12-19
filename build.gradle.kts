plugins {
    id("org.springframework.boot") version "2.5.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("java")
    id("groovy")
}

val asciidoctorExtensions: Configuration by configurations.creating

group = "io.github.key-del-jeeinho"
version = "0.0.1-SNAPSHOT"

configurations {
    compileOnly {
        extendsFrom(configurations["testImplementation"])
    }
}

repositories {
    mavenCentral()
}
// 의존성 관리
dependencies {
    //Main

    /// Spring Boot
    implementation ("org.springframework.boot:spring-boot-starter-data-jpa:2.5.6")
    implementation ("org.springframework.boot:spring-boot-starter-security:2.5.6")
    implementation ("org.springframework.boot:spring-boot-starter-validation:2.5.6")
    implementation ("org.springframework.boot:spring-boot-starter-web:2.5.6")
    annotationProcessor ("org.springframework.boot:spring-boot-configuration-processor:2.5.6")
    implementation ("org.springframework.boot:spring-boot-starter-thymeleaf")
    /// Javassist
    implementation ("javassist:javassist:3.3")
    /// Lombok
    annotationProcessor ("org.projectlombok:lombok:1.18.22")
    implementation ("org.projectlombok:lombok:1.18.22")
    /// MySQL
    runtimeOnly ("mysql:mysql-connector-java:8.0.25")
    // JWT
    implementation ("io.jsonwebtoken:jjwt:0.9.1")
    //Golabab Rosetta Library
    implementation ("io.github.key-del-jeeinho:golabab-v2-rosetta-lib:1.3.0-RELEASE")
    //Swagger2
    implementation ("io.springfox:springfox-swagger2:2.9.2")
    implementation ("io.springfox:springfox-swagger-ui:2.9.2")


    //Test

    /// Spring Boot Test
    testImplementation ("org.springframework.boot:spring-boot-starter-test:2.5.6")
    testImplementation ("org.springframework.security:spring-security-test:5.5.1") //To test with Spring Security
    /// Jupiter Engine
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    /// Spock
    testImplementation("org.spockframework:spock-spring:2.0-groovy-3.0")
    testImplementation("org.spockframework:spock-core:2.0-groovy-3.0")
    ///ByteBuddy
    testImplementation("net.bytebuddy:byte-buddy")
    /// Groovy Support
    testImplementation("org.codehaus.groovy:groovy-all:3.0.8")
    /// Automate RestAPI
    testImplementation ("org.springframework.restdocs:spring-restdocs-mockmvc:2.0.5.RELEASE")
    asciidoctorExtensions("org.springframework.restdocs:spring-restdocs-asciidoctor:2.0.5.RELEASE")
}

tasks {
    val snippetsDir = file("$buildDir/generated-snippets")

    clean {
        delete("src/main/resources/static/docs")
    }

    test {
        systemProperty("org.springframework.restdocs.outputDir", snippetsDir)
        outputs.dir(snippetsDir)
    }

    build {
        dependsOn("copyDocument")
    }

    asciidoctor {
        dependsOn(test)

        attributes(
            mapOf("snippets" to snippetsDir)
        )
        inputs.dir(snippetsDir)

    }

    register<Copy>("copyDocument") {
        dependsOn(asciidoctor)

        destinationDir = file(".")
        from(asciidoctor.get().outputDir) {
            into("src/main/resources/static/docs")
        }
        copy {
            from("build/docs/asciidoc/api.html")
            into("docs")
        }
    }

    bootJar {
        dependsOn(asciidoctor)

        from(asciidoctor.get().outputDir) {
            into("BOOT-INF/classes/static/docs")
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    withType<Test> {
        systemProperty("file.encoding", "UTF-8")
    }
    withType<Javadoc>{
        options.encoding = "UTF-8"
    }
}