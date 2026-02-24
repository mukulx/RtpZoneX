plugins {
    java
    id("com.github.hierynomus.license") version "0.16.1"
    id("com.gradleup.shadow") version "9.2.0"
}

group = "github.mukulx"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.alessiodp.com/releases/")
    maven("https://repo.alessiodp.com/snapshots/")
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    implementation("com.alessiodp.libby:libby-bukkit:2.0.0-SNAPSHOT")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

license {
    header = file("LICENSE_HEADER")
    strictCheck = true
    mapping("java", "SLASHSTAR_STYLE")
    include("**/*.java")
}

tasks {
    shadowJar {
        archiveFileName.set("RtpZoneX-${project.version}.jar")
        relocate("com.alessiodp.libby", "github.mukulx.rtpzonex.libs.libby")
        minimize()
    }
    
    build {
        dependsOn(shadowJar)
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
