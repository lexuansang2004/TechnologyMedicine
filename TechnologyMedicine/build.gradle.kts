plugins {
    id("java")
    id("application")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

group = "iuh.fit"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Neo4j Driver
    implementation("org.neo4j.driver:neo4j-java-driver:5.28.1")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")

    // Gson for JSON processing
    implementation("com.google.code.gson:gson:2.12.1")

    // Password hashing
    implementation("org.mindrot:jbcrypt:0.4")

    // Email
    implementation("com.sun.mail:javax.mail:1.6.2")

    // Logging
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")

    // Testing
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("iuh.fit.Server")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "iuh.fit.Server"
    }

    // Include all dependencies in the JAR
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}