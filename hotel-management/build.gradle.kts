val swaggerVersion = "2.6.0"
val jjwtVersion = "0.12.6"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.3")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    //implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework:spring-aspects")
    implementation("com.h2database:h2")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${swaggerVersion}")
    implementation("io.jsonwebtoken:jjwt-api:${jjwtVersion}")

    runtimeOnly("io.jsonwebtoken:jjwt-impl:${jjwtVersion}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${jjwtVersion}")

    testImplementation("org.springframework.security:spring-security-test")
}