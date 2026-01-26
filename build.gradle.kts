plugins {
    java
    id("com.github.hierynomus.license") version "0.16.1"
}

group = "github.mukulx"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

license {
    header = file("LICENSE_HEADER")
    strictCheck = true
    mapping("java", "SLASHSTAR_STYLE")
    include("**/*.java")
}

tasks {
    jar {
        archiveFileName.set("RtpZoneX-${project.version}.jar")
    }
    
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
    
    compileJava {
        dependsOn("licenseFormat")
    }
}
