plugins {
    java
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.kousenit"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("org.apache.tika:tika-core:2.9.1")
    implementation("org.apache.tika:tika-parsers-standard-package:2.9.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Make security warnings go away
    implementation("org.apache.commons:commons-compress:1.26.1")
    implementation("org.apache.james:apache-mime4j-core:0.8.11")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
