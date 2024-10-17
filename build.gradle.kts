plugins {
    java
    application
}

group = "com.weareadaptive"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.vertx:vertx-core:4.5.10")
    implementation("io.vertx:vertx-web:4.5.10")
    implementation("io.vertx:vertx-web-client:4.5.10")
    implementation("io.github.binance:binance-connector-java:3.4.0")
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation ("io.netty:netty-resolver-dns-native-macos:4.1.85.Final:osx-aarch_64")
    implementation ("org.slf4j:slf4j-api:1.7.32")
    implementation ("ch.qos.logback:logback-classic:1.2.6")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.vertx:vertx-junit5:4.5.10")
    testImplementation("com.google.truth:truth:1.4.3")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.test {
    useJUnitPlatform()
}
