import org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES

plugins {
    id("io.spring.dependency-management")
    id("org.springframework.boot") apply false
}

allprojects {
    group = "ru.latyshev"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    val wiremock: String by project
    val springdocOpenapi: String by project

    apply(plugin = "io.spring.dependency-management")
    dependencyManagement {
        dependencies {
            imports {
                mavenBom(BOM_COORDINATES)
            }
            dependency("com.github.tomakehurst:wiremock-standalone:$wiremock")
            dependency("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocOpenapi")
        }
    }
}

subprojects {
    plugins.apply(JavaPlugin::class.java)
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    val lombok: String by project

    plugins.withId("org.springframework.boot") {
        dependencies {
            add("implementation", "org.springframework.boot:spring-boot-starter-validation")
        }
    }

    dependencies {
        "compileOnly"("org.projectlombok:lombok:$lombok")
        "annotationProcessor"("org.projectlombok:lombok:$lombok")
        "testCompileOnly"("org.projectlombok:lombok:$lombok")
        "testAnnotationProcessor"("org.projectlombok:lombok:$lombok")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-parameters", "-Xlint:all,-serial,-processing"))
    }

    tasks.withType<JavaExec> {
        jvmArgs("-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging.showExceptions = true
    }
}
