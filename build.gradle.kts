plugins {
    id("java")
}

group = "iuh.fit.vn"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
    implementation("net.datafaker:datafaker:2.4.2")
    implementation("com.microsoft.sqlserver:mssql-jdbc:12.9.0.jre11-preview")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.5.1")
    implementation("org.hibernate.orm:hibernate-core:6.2.6.Final")
}

tasks.test {
    useJUnitPlatform()
}