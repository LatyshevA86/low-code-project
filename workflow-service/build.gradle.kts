plugins {
    id("org.springframework.boot")
}

val temporalSpringBootStarter: String by project
val testcontainersBom: String by project

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("io.temporal:temporal-spring-boot-starter:$temporalSpringBootStarter")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation(enforcedPlatform("org.testcontainers:testcontainers-bom:$testcontainersBom"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testImplementation("com.github.tomakehurst:wiremock-standalone")
    testImplementation("io.temporal:temporal-testing:$temporalSpringBootStarter")
}
